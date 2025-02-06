package com.dasima.drawrun.domain.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ShowInfoResponse {
    private int userId;
    private String nickname;
    private String profileImgUrl;
}
