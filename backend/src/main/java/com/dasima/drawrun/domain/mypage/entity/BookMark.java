package com.dasima.drawrun.domain.mypage.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class BookMark {
    private int bookmarkId;
    private int userPathId;
    private int userId;
    private LocalDateTime createDate;

    // join
    private UserPath userPath;
}
