package com.dasima.drawrun.domain.masterpiece.service;

import com.dasima.drawrun.domain.masterpiece.dto.request.MasterpieceSaveRequest;
import com.dasima.drawrun.domain.masterpiece.dto.response.MasterpieceListResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface MasterpieceService {
    int save(MasterpieceSaveRequest dto, int userId);
    List<MasterpieceListResponse> list();
}
