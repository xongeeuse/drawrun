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
public class MasterpieceParticipant {
    private int masterpieceParticipantId;
    private int userId;
    private int state;
    private int masterpieceSegId;
    private LocalDateTime createDate;

    private MasterpieceSeg masterpieceSeg;
    private MasterpieceBoard masterpieceBoard;
}
