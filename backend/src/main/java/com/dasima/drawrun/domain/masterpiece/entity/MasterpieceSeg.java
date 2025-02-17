package com.dasima.drawrun.domain.masterpiece.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class MasterpieceSeg {
    private int masterpieceSegId;
    private int masterpieceBoardId;
    private String mongoId;
    private String address;
    private int pathNum;
}
