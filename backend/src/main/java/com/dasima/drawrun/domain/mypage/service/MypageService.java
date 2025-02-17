package com.dasima.drawrun.domain.mypage.service;

import com.dasima.drawrun.domain.mypage.dto.BookmarkResponse;

import java.util.List;

public interface MypageService {
    List<BookmarkResponse> bookmark(int userId);
}
