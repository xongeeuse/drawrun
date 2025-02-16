from fastapi import FastAPI
from makeRoute import makeRouteNodeList
from pydantic import BaseModel

app = FastAPI()

class MapRequest(BaseModel):
    lon: float
    lat: float
    mapDataUrl: str
    imgUrl: str
    
@app.post("/api/v1/createMap")
async def create_map(request: MapRequest):
    nodeList = makeRouteNodeList(request.lon, request.lat, request.mapDataUrl, request.imgUrl)
    return {"data": nodeList}
