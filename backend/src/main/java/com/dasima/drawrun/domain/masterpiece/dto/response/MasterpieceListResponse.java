package com.dasima.drawrun.domain.masterpiece.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MasterpieceListResponse {
    private int masterpieceBoardId;
    private int userId;
    private int restrictCount;
    private int userPathId;
    private String nickname;
    private String profileImgUrl;
    private String pathImgUrl;
    private String gu;
    private double distance;
    private int joinCount;
    private int dDay;
}
