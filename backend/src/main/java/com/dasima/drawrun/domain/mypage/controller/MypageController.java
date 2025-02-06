package com.dasima.drawrun.domain.mypage.controller;

import com.dasima.drawrun.domain.mypage.mapper.MyPageMapper;
import com.dasima.drawrun.domain.mypage.service.MyPageService;
import com.dasima.drawrun.global.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("")
    public ResponseEntity<?> showinfo(@AuthenticationPrincipal UserPrinciple userPrinciple) {
        return ResponseEntity.ok(myPageService.showinfo(userPrinciple.getUserId()));
    }

    @GetMapping("/bookmark")
                                                public ResponseEntity<?> bookmark(@AuthenticationPrincipal UserPrinciple userPrinciple)
    {
        return ResponseEntity.ok(myPageService.bookmark(userPrinciple.getUserId()));
    }

    @GetMapping("/bookmark/{bookmarkId}")
    public ResponseEntity<?> bookmarksearch(@AuthenticationPrincipal UserPrinciple userPrinciple, @PathVariable int bookmarkId){
        return ResponseEntity.ok(myPageService.onebookmark(userPrinciple.getUserId(), bookmarkId));
    }
}
