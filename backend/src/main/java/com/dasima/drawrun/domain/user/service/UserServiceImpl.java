package com.dasima.drawrun.domain.user.service;

import com.dasima.drawrun.domain.user.dto.HistoryDto;
import com.dasima.drawrun.domain.user.dto.response.UserHistoryReponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public UserHistoryReponse getHistoryById(int userId) {
        List<HistoryDto> list = null;

        UserHistoryReponse userHistoryReponse = UserHistoryReponse
                .builder()
                .build();

        return userHistoryReponse;
    }


}
