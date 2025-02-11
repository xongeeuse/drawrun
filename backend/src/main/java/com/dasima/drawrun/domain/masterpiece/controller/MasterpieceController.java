package com.dasima.drawrun.domain.masterpiece.controller;


import com.dasima.drawrun.domain.masterpiece.dto.request.MasterpieceSaveRequest;
import com.dasima.drawrun.domain.masterpiece.service.MasterpieceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/masterpiece")
public class MasterpieceController {
    @Autowired
    MasterpieceService masterpieceService;
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody MasterpieceSaveRequest dto){
        return ResponseEntity.ok(masterpieceService.save(dto));
    }
}
