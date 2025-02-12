package com.dasima.drawrun.domain.masterpiece.entity;

import com.dasima.drawrun.domain.course.entity.UserPath;
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
    private int participantCount; // 참가자 수
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
    private LocalDateTime expireDate;

    // join table
    private UserPath userPath;
    private MasterpieceSeg masterpieceSeg;
    private MasterpieceParticipant masterpieceParticipant;
}
