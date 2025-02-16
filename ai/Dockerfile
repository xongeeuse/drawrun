# Base image: conda를 제공하는 miniconda3 이미지 사용
FROM continuumio/miniconda3

# conda-forge 채널을 기본 채널로 설정 (환경에 따라 선택)
RUN conda config --add channels conda-forge

# 필요한 라이브러리들을 설치
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

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트의 코드 전체를 컨테이너 내부 /app 디렉토리로 복사
COPY . /app

# 컨테이너가 사용할 포트 번호를 지정 (FastAPI 기본 포트 8000)
EXPOSE 8000

# 컨테이너 시작 시 실행할 명령어:
# main.py 파일의 app 인스턴스를 uvicorn으로 실행하며, 외부 접속을 허용하도록 host를 0.0.0.0으로 설정
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8000"]
