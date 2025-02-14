from fastapi import FastAPI
from pydantic import BaseModel

from makeRoute import makeRouteNodeList

app = FastAPI()

class MapRequest(BaseModel):
    lon: float
    lat: float
    mapData: str
    imgUrl: str
    
@app.post("/api/v1/createMap")
async def create_map(request: MapRequest):
    makeRouteNodeList(request.lon, request.lat, request.mapData, request.imgUrl)
    return {"test": []}
