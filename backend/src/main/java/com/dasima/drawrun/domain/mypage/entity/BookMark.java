package com.dasima.drawrun.domain.mypage.entity;

import com.dasima.drawrun.domain.mypage.dto.response.BookMarkResponse;
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

    public BookMarkResponse toBookMarkEntity(){
        return BookMarkResponse
                .builder()
                .bookmarkPK(bookmarkId)
                .userPathId(userPathId)
                .pathImgUrl(userPath.getPathImgUrl())
                .name(userPath.getName())
                .build();
    }
}
