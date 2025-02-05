package com.dasima.drawrun.domain.mypage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mypage")
public class MypageController {

    @GetMapping("/showinfo")
    public ResponseEntity<?> showinfo(@PathVariable int userId) {
        return ResponseEntity.ok();
    }

}
