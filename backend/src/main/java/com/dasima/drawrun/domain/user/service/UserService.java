package com.dasima.drawrun.domain.user.service;

import com.dasima.drawrun.domain.user.dto.response.UserHistoryResponse;

public interface UserService {

    UserHistoryResponse getHistoryById(int userId);

}
