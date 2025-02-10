package com.dasima.drawrun.domain.user.dto.request;

import lombok.Data;

@Data
public class FindPasswordRequestDto {
    private String userId;
    private String email;
}
