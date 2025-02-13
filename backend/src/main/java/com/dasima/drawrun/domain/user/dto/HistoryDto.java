package com.dasima.drawrun.domain.user.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HistoryDto {
    String pathImgUrl;
    LocalDateTime createDate;
    Double distance;
}
