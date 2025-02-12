package com.dasima.drawrun.domain.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HistoryDto {
    Integer userPathId;
    Integer pathId;
    String pathImgUrl;
    String name;
    LocalDateTime createDate;
    Double distance;
    String address;
}
