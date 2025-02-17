package com.dasima.drawrun.domain.mypage.controller;

import com.dasima.drawrun.domain.mypage.dto.BookmarkResponse;
import com.dasima.drawrun.domain.mypage.service.MypageService;
import com.dasima.drawrun.global.common.ApiResponseJson;
import com.dasima.drawrun.global.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mypage")
public class MypageController {
    @Autowired
    MypageService mypageService;

    @GetMapping("/bookmark")
    public ResponseEntity<?> bookmark(@AuthenticationPrincipal UserPrinciple userPrinciple){
        return ResponseEntity.ok(mypageService.bookmark(userPrinciple.getUserId()));
    }
}
