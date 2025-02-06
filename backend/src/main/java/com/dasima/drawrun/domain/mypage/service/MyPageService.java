package com.dasima.drawrun.domain.mypage.service;

import com.dasima.drawrun.domain.mypage.dto.response.BookMarkResponse;
import com.dasima.drawrun.domain.mypage.dto.response.ShowInfoResponse;
import com.dasima.drawrun.domain.user.entity.User;

import java.util.List;

public interface MyPageService {
    ShowInfoResponse showinfo(int userId); // findByUserId
    List<BookMarkResponse> bookmark(int userId);
}
