package com.dasima.drawrun.domain.user.service;

import com.dasima.drawrun.domain.user.dto.response.UserArtsResponse;
import com.dasima.drawrun.domain.user.dto.response.UserHistoryResponse;
import com.dasima.drawrun.domain.user.dto.response.UserStatusResponse;

public interface UserService {

    UserHistoryResponse getHistoryById(int userId);

    UserArtsResponse getArtById(int userId);

    UserStatusResponse getUserStatById(int userId);

    String getRegionById(int userId);

    void setRegionById(int userId, String region);

}
