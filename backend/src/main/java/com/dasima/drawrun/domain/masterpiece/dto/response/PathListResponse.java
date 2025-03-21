package com.dasima.drawrun.domain.masterpiece.dto.response;

import com.dasima.drawrun.domain.course.vo.GeoPoint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PathListResponse {
    private List<GeoPoint> path;
    private int masterpieceSegId;
    private String address;
    private String address2;
    private String nickname;
}
