package com.dasima.drawrun.domain.user.dto.response;

import com.dasima.drawrun.domain.user.dto.HistoryDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserHistoryResponse {
    int userPK;
    String nickname;
    String profileImgUrl;
    List<HistoryDto> history;
}
