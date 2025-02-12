package com.dasima.drawrun.domain.masterpiece.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MasterpieceBoard {
    private int masterpieceBoardId;
    private int userId;
    private int restrictCount;
    private int userPathId;
    private int state;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

}
