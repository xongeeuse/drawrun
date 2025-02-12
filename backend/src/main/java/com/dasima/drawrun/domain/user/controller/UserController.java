package com.dasima.drawrun.domain.user.controller;

import com.dasima.drawrun.domain.user.dto.response.UserHistoryResponse;
import com.dasima.drawrun.domain.user.service.UserService;
import com.dasima.drawrun.global.common.ApiResponseJson;
import com.dasima.drawrun.global.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/mypage")
    public ResponseEntity<ApiResponseJson> getMyHistory(@AuthenticationPrincipal UserPrinciple userPrinciple) {
        UserHistoryResponse response = userService.getHistoryById(userPrinciple.getUserId());

        return ResponseEntity.ok(
                new ApiResponseJson(true, 200, "정보 조회에 성공했습니다.", response)
        );
    }

}
