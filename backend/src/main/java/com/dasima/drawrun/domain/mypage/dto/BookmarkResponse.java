package com.dasima.drawrun.domain.mypage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookmarkResponse {
    private int UserPathPk;
    private String courseName;
    private String pathImgUrl;
    private String address;
}
