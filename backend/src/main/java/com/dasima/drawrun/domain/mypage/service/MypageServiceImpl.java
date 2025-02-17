package com.dasima.drawrun.domain.mypage.service;

import com.dasima.drawrun.domain.course.dto.response.CourseListResponse;
import com.dasima.drawrun.domain.course.entity.Bookmark;
import com.dasima.drawrun.domain.mypage.dto.BookmarkResponse;
import com.dasima.drawrun.domain.mypage.mapper.MypageMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MypageServiceImpl implements MypageService{
    private final MypageMapper mypageMapper;

    public List<BookmarkResponse> bookmark(int userId){
        List<Bookmark> bookmarks = mypageMapper.bookmark(userId);

        List<BookmarkResponse> bookmarkResponses = new ArrayList<>();
        for(Bookmark bookmark : bookmarks ){
            BookmarkResponse bookmarkResponse = BookmarkResponse.builder()
                    .UserPathPk(bookmark.getUserPathId())
                    .courseName(bookmark.getUserPath().getName())
                    .pathImgUrl(bookmark.getUserPath().getPathImgUrl())
                    .address(bookmark.getUserPath().getAddress())
                    .build();

            bookmarkResponses.add(bookmarkResponse);
        }
        return bookmarkResponses;
    }
}
