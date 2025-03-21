package com.dasima.drawrun.domain.mypage.controller;

import com.dasima.drawrun.domain.mypage.dto.UserBadgeDto;
import com.dasima.drawrun.domain.mypage.entity.BadgeInventory;
import com.dasima.drawrun.domain.mypage.repository.BadgeInventoryRepository;
import com.dasima.drawrun.domain.mypage.service.BadgeService;
import com.dasima.drawrun.global.common.ApiResponseJson;
import com.dasima.drawrun.global.exception.CustomException;
import com.dasima.drawrun.global.exception.ErrorCode;
import com.dasima.drawrun.global.security.UserPrinciple;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/badge")
public class BadgeController {

    @Autowired
    private BadgeService badgeService;

    @Autowired
    private BadgeInventoryRepository badgeInventoryRepository;

    @PostMapping("/grant")
    public ResponseEntity<ApiResponseJson> grant(@AuthenticationPrincipal UserPrinciple userPrinciple, @RequestBody Map<String, Integer> request) {
        if(request.get("masterpiece") == 1) {
            BadgeInventory master = badgeInventoryRepository.findBadgeInventoryByUserIdAndBadgeId(
                userPrinciple.getUserId(), 9).orElseGet(null);

            if(master != null) {
                return ResponseEntity.ok(
                    new ApiResponseJson(true, 200, "지급할 배지가 없습니다.", null)
                );
            }

            badgeInventoryRepository.save(
                BadgeInventory
                    .builder()
                    .userId(userPrinciple.getUserId())
                    .badgeId(10)
                    .build()
            );

            return ResponseEntity.ok(
                new ApiResponseJson(true, 200, "걸작 배지 지급을 완료했습니다.", null)
            );
        }


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
