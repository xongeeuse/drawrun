package com.dasima.drawrun.domain.mypage.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookMarkResponse {
    private int bookmarkPK;
    private int userPathId;
    private String pathImgUrl;
    private String name;
}
