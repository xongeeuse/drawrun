package com.dasima.drawrun.domain.user.dto.request;

import lombok.Data;

@Data
public class EmailAuthNumberRequestDto {
    private String email;
    private String authNumber;
}
