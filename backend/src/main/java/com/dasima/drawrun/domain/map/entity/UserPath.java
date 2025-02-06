package com.dasima.drawrun.domain.map.entity;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPath {
    private int userPathId;
    private int userId;
    private String pathId;
}
