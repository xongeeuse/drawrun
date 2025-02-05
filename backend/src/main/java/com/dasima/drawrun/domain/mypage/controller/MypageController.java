package com.dasima.drawrun.domain.mypage.controller;

import com.dasima.drawrun.domain.mypage.mapper.MyPageMapper;
import com.dasima.drawrun.domain.mypage.service.MyPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/mypage")
public class MypageController {
    @Autowired
    MyPageService myPageService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> showinfo(@PathVariable("userId") int userId) {
        return ResponseEntity.ok(myPageService.showinfo(userId));
    }

}
