package com.dasima.drawrun.domain.masterpiece.controller;


import com.dasima.drawrun.domain.masterpiece.dto.request.MasterpieceSaveRequest;
import com.dasima.drawrun.domain.masterpiece.service.MasterpieceService;
import com.dasima.drawrun.global.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/masterpiece")
public class MasterpieceController {
    @Autowired
    MasterpieceService masterpieceService;
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody MasterpieceSaveRequest dto, @AuthenticationPrincipal UserPrinciple userPrinciple){
        return ResponseEntity.ok(masterpieceService.save(dto, userPrinciple.getUserId()));
    }


    // 다건 조회
    @GetMapping("/list")
    public ResponseEntity<?> list(){
        return ResponseEntity.ok(masterpieceService.list());
    }

    // 단건 조회
    @GetMapping("/search/{masterpieceBoardId}")
    public ResponseEntity<?> search(@PathVariable int masterpieceBoardId){
        return ResponseEntity.ok(masterpieceService.search(masterpieceBoardId));
    }

    @GetMapping("/pathlist/{masterpieceBoardId}")
    public ResponseEntity<?> pathlist(@PathVariable int masterpieceBoardId){
        return ResponseEntity.ok();
    }
}
