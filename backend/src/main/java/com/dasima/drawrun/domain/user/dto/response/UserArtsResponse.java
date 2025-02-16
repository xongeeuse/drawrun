package com.dasima.drawrun.domain.user.dto.response;

import com.dasima.drawrun.domain.course.entity.UserPath;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserArtsResponse {
    List<UserPath> artList;
}
