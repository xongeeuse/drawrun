package com.dasima.drawrun.domain.mypage.controller;

import com.dasima.drawrun.domain.mypage.dto.UserBadgeDto;
import com.dasima.drawrun.domain.mypage.service.BadgeService;
import com.dasima.drawrun.global.common.ApiResponseJson;
import com.dasima.drawrun.global.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/badge")
public class BadgeController {

    @Autowired
    private BadgeService badgeService;

    @PostMapping("/grant")
    public ResponseEntity<ApiResponseJson> grant(@AuthenticationPrincipal UserPrinciple userPrinciple) {
        List<UserBadgeDto> badgeList = badgeService.grant(userPrinciple.getUserId());

        if (badgeList.isEmpty()) {
            return ResponseEntity.ok(
                    new ApiResponseJson(true, 200, "지급할 배지가 없습니다.", null)
            );
        }
        return ResponseEntity.ok(
                new ApiResponseJson(true, 200, "배지 지급을 완료했습니다.", badgeList)
        );
    }

    @GetMapping("/check")
    public ResponseEntity<ApiResponseJson> check(@AuthenticationPrincipal UserPrinciple userPrinciple) {
        List<UserBadgeDto> badgeList = badgeService.check(userPrinciple.getUserId());

        return ResponseEntity.ok(
                new ApiResponseJson(true, 200, "보유한 배지를 불러왔습니다.", badgeList)
        );
    }

}
