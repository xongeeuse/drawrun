from fastapi import FastAPI
from pydantic import BaseModel

from makeRoute import convert_geojson_to_path, makeRouteNodeList

app = FastAPI()

class MapRequest(BaseModel):
    lon: float
    lat: float
    mapDataUrl: str
    imgUrl: str
    
@app.post("/api/v1/createMap")
async def create_map(request: MapRequest):
    nodeList = makeRouteNodeList(request.lon, request.lat, request.mapDataUrl, request.imgUrl)
    result = convert_geojson_to_path(nodeList)
    
    return result
