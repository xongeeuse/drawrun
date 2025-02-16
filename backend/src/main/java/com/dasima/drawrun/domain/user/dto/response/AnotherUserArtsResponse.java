package com.dasima.drawrun.domain.user.dto.response;

import com.dasima.drawrun.domain.course.entity.UserPath;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AnotherUserArtsResponse {
    int userPK;
    String nickname;
    String profileImgUrl;
    List<UserPath> artList;
}
