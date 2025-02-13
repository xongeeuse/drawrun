package com.dasima.drawrun.domain.user.controller;

import com.dasima.drawrun.domain.user.dto.response.AnotherUserArtsResponse;
import com.dasima.drawrun.domain.user.dto.response.UserArtsResponse;
import com.dasima.drawrun.domain.user.dto.response.UserHistoryResponse;
import com.dasima.drawrun.domain.user.entity.User;
import com.dasima.drawrun.domain.user.repository.UserRepository;
import com.dasima.drawrun.domain.user.service.UserService;
import com.dasima.drawrun.global.common.ApiResponseJson;
import com.dasima.drawrun.global.exception.CustomException;
import com.dasima.drawrun.global.exception.ErrorCode;
import com.dasima.drawrun.global.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    UserService userService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/mypage")
    public ResponseEntity<ApiResponseJson> getMyHistory(@AuthenticationPrincipal UserPrinciple userPrinciple) {
        UserHistoryResponse response = userService.getHistoryById(userPrinciple.getUserId());

        return ResponseEntity.ok(
                new ApiResponseJson(true, 200, "정보 조회에 성공했습니다.", response)
        );
    }

    @GetMapping("/art")
    public ResponseEntity<ApiResponseJson> getArtHistory(@AuthenticationPrincipal UserPrinciple userPrinciple) {
        UserArtsResponse response = userService.getArtById(userPrinciple.getUserId());

        return ResponseEntity.ok(
                new ApiResponseJson(true, 200, "정보 조회에 성공했습니다.", response)
        );
    }

    @GetMapping("/find")
    public ResponseEntity<ApiResponseJson> searchArtHistory(@RequestParam int userPk) {
        UserArtsResponse list = userService.getArtById(userPk);
        User user = userRepository.findByUserId(userPk).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER_ID));

        AnotherUserArtsResponse response = AnotherUserArtsResponse
                .builder()
                .userPK(userPk)
                .nickname(user.getUserNickname())
                .profileImgUrl(user.getProfileImgUrl())
                .artList(list.getArtList())
                .build();

        return ResponseEntity.ok(
                new ApiResponseJson(true, 200, "정보 조회에 성공했습니다.", response)
        );
    }

}
