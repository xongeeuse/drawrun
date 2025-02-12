package com.dasima.drawrun.domain.user.service;

import com.dasima.drawrun.domain.user.dto.response.UserHistoryReponse;

public interface UserService {

    UserHistoryReponse getHistoryById(int userId);

}
