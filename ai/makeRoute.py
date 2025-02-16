import io
import json
import os
import tempfile

import boto3
import cv2
import matplotlib.pyplot as plt
import networkx as nx
import numpy as np
import osmnx as ox
import pyproj
from shapely.geometry import Point


def makeRouteNodeList(lon: float, lat: float, mapDataUrl: str, imgUrl: str):
    
    # ================================
    # 1. OSM 파일 불러오기 및 투영
    # ================================

    s3 = boto3.client('s3')
    bucket_name = 'drawrunbucket'          # ARN이 아니라 버킷 이름만 사용

    # S3 객체를 읽어 메모리 상의 BytesIO 스트림 생성
    response = s3.get_object(Bucket=bucket_name, Key=mapDataUrl)
    osm_data = response['Body'].read()

    with tempfile.NamedTemporaryFile(mode='w', delete=False, suffix='.osm') as tmp_file:
        tmp_file.write(osm_data.decode('utf-8'))
        osm_file_name = tmp_file.name

    # 임시 파일을 사용하여 OSMnx로 그래프 생성
    G = ox.graph_from_xml(osm_file_name, bidirectional=True)
    G_proj = ox.project_graph(G)
    
    # ================================
    # 2. 사용자의 그림 분석: 선 추출
    # ================================
    # imgUrl이 제공되지 않으면 기본 경로(테스트용 강아지 모양 상대 좌표)를 사용합니다.
    if not imgUrl:
        print("imgUrl이 제공되지 않아 기본 강아지 모양 상대 좌표를 사용합니다.")
        drawn_rel_path = np.array([
            [0.0, 0.0],
            [0.2, 0.4],
            [0.4, 0.5],
            [0.6, 0.4],
            [0.8, 0.0],
            [0.6, -0.4],
            [0.4, -0.5],
            [0.2, -0.4],
            [0.0, 0.0]
        ], dtype=np.float32)
    else:
        response = s3.get_object(Bucket=bucket_name, Key=imgUrl)
        img_data = response['Body'].read()
        with tempfile.NamedTemporaryFile(mode='w', delete=False, suffix='.jpg') as tmp_file:
            tmp_file.write(img_data.decode('utf-8', errors='ignore'))
            user_image_name = tmp_file.name
        user_image_path = user_image_name
        try:
            drawn_rel_path = extract_drawn_path(user_image_path)
        except Exception as e:
            print("그림 분석 오류:", e)
            drawn_rel_path = np.array([
                [0.0, 0.0],
                [0.2, 0.4],
                [0.4, 0.5],
                [0.6, 0.4],
                [0.8, 0.0],
                [0.6, -0.4],
                [0.4, -0.5],
                [0.2, -0.4],
                [0.0, 0.0]
            ], dtype=np.float32)
            print("테스트용 강아지 모양 상대 좌표 사용.")
        
    inflection_rel_path = get_inflection_points(drawn_rel_path, angle_threshold=10)
    user_start_coord = (lon, lat)
    absolute_path = generate_absolute_path(user_start_coord, inflection_rel_path, scale_m=1200)
    snapped_path = snap_path_to_roads(absolute_path, G_proj, user_start_coord)

    geojson = {
        "type": "Feature",
        "geometry": {
            "type": "LineString",
            "coordinates": [(lon, lat) for (lat, lon) in snapped_path]
        },
        "properties": {
            "name": "Running Course"
        }
    }
    
    os.remove(osm_file_name)
    os.remove(user_image_name)

    return geojson


# 이미지 분석 - 선 추출
def extract_drawn_path(image_path):
    image = cv2.imread(image_path)
    if image is None:
        raise ValueError("이미지를 불러올 수 없습니다.")
    # 상하 반전 문제 해결: flip
    image = cv2.flip(image, 0)
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    edges = cv2.Canny(gray, 50, 150)
    contours, _ = cv2.findContours(edges, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    if not contours:
        raise ValueError("컨투어를 찾을 수 없습니다.")
    contour = max(contours, key=cv2.contourArea)
    epsilon = 0.01 * cv2.arcLength(contour, True)
    approx = cv2.approxPolyDP(contour, epsilon, True)
    h, w = gray.shape
    rel_points = [(pt[0][0] / w, pt[0][1] / h) for pt in approx]
    return np.array(rel_points, dtype=np.float32)


# 이미지 분석 - 변곡점 검출
def get_inflection_points(points, angle_threshold=10):
    if len(points) < 3:
        return points
    filtered = [points[0]]
    for i in range(1, len(points)-1):
        p_prev, p_curr, p_next = points[i-1], points[i], points[i+1]
        v1 = p_curr - p_prev
        v2 = p_next - p_curr
        norm1 = np.linalg.norm(v1)
        norm2 = np.linalg.norm(v2)
        if norm1 == 0 or norm2 == 0:
            continue
        v1 /= norm1
        v2 /= norm2
        dot = np.clip(np.dot(v1, v2), -1.0, 1.0)
        angle_deg = np.degrees(np.arccos(dot))
        if abs(angle_deg - 50) > angle_threshold:
            filtered.append(p_curr)
    filtered.append(points[-1])
    return np.array(filtered, dtype=np.float32)


# 좌표계 변환
def get_utm_crs(lat, lon):
    utm_zone = int((lon + 180) / 6) + 1
    if lat >= 0:
        epsg_code = 32600 + utm_zone
    else:
        epsg_code = 32700 + utm_zone
    return f"epsg:{epsg_code}"


# 절대 경로 생성
def generate_absolute_path(start_coord, rel_path, scale_m=5000):
    proj_crs = get_utm_crs(start_coord[0], start_coord[1])
    transformer = pyproj.Transformer.from_crs("epsg:4326", proj_crs, always_xy=True)
    inv_transformer = pyproj.Transformer.from_crs(proj_crs, "epsg:4326", always_xy=True)
    x0, y0 = transformer.transform(start_coord[1], start_coord[0])
    min_vals = rel_path.min(axis=0)
    rel_shifted = rel_path - min_vals
    max_val = rel_shifted.max()
    factor = scale_m / max_val if max_val != 0 else 1.0
    offsets = rel_shifted * factor
    abs_points_proj = [(x0, y0)]
    for off in offsets[1:]:
        abs_points_proj.append((x0 + off[0], y0 + off[1]))
    abs_points = []
    for (x_proj, y_proj) in abs_points_proj:
        lon, lat = inv_transformer.transform(x_proj, y_proj)
        abs_points.append((lat, lon))
    return abs_points



# 도로 네트워크 스냅
def snap_path_to_roads(abs_path, G_proj, original_start_coord):
    snapped_coords = [original_start_coord]
    # OSMnx 최신 버전: graph_to_gdfs returns a single GeoDataFrame if edges=False.
    nodes_gdf = ox.graph_to_gdfs(G_proj, nodes=True, edges=False)
    min_x, max_x = nodes_gdf["x"].min(), nodes_gdf["x"].max()
    min_y, max_y = nodes_gdf["y"].min(), nodes_gdf["y"].max()
    
    transformer = pyproj.Transformer.from_crs("epsg:4326", G_proj.graph['crs'], always_xy=True)
    inv_transformer = pyproj.Transformer.from_crs(G_proj.graph['crs'], "epsg:4326", always_xy=True)
    
    for (lat, lon) in abs_path[1:]:
        x, y = transformer.transform(lon, lat)
        x = np.clip(x, min_x, max_x)
        y = np.clip(y, min_y, max_y)
        node_id = ox.distance.nearest_nodes(G_proj, X=x, Y=y)
        node = G_proj.nodes[node_id]
        lon_snap, lat_snap = inv_transformer.transform(node["x"], node["y"])
        snapped_coords.append((lat_snap, lon_snap))
    return snapped_coords

