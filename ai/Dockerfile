# Linux 기반 컨테이너 (Docker Desktop에서 Linux 컨테이너 모드 사용)
FROM continuumio/miniconda3

# 시스템 패키지 업데이트 및 OpenCV 의존성 libgl1-mesa-glx 설치
RUN apt-get update && apt-get install -y libgl1-mesa-glx && apt-get clean

# conda-forge 채널 추가
RUN conda config --add channels conda-forge

# 필요한 패키지 설치
RUN conda install -y \
    opencv \
    numpy \
    osmnx \
    networkx \
    pyproj \
    json5 \
    matplotlib \
    shapely \
    requests \
    scipy \
    geojson \
    boto3 \
    fastapi \
    uvicorn

WORKDIR /app

COPY . /app

EXPOSE 8000

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
