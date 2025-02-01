package com.dasima.drawrun.domain.map.controller;

import com.dasima.drawrun.domain.map.dto.PathSaveRequest;
import com.dasima.drawrun.domain.map.service.MapService;
import com.dasima.drawrun.global.common.ApiResponseJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/map")
public class MapController {
    @Autowired
    MapService mapService;
    @GetMapping("/mongotest")
    public ResponseEntity<?> mongotest() {
        return ResponseEntity.ok(mapService.mongomongotest());
    }

    // save path
    @PostMapping("/save")
    public ResponseEntity<ApiResponseJson> save(@RequestBody PathSaveRequest dto){
        mapService.save(dto);
    }

}
