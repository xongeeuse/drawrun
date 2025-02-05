package com.dasima.drawrun.domain.mypage.service;

import com.dasima.drawrun.domain.mypage.dto.response.ShowInfoResponse;
import com.dasima.drawrun.domain.user.entity.User;

public interface MyPageService {
    ShowInfoResponse showinfo(int userId); // findByUserId
}
