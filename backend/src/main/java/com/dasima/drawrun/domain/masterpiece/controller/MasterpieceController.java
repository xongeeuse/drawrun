package com.dasima.drawrun.domain.masterpiece.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/masterpiece")
public class MasterpieceController {
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody ){

    }
}
