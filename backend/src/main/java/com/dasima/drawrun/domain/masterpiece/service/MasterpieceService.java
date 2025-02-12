package com.dasima.drawrun.domain.masterpiece.service;

import com.dasima.drawrun.domain.masterpiece.dto.request.MasterpieceSaveRequest;
import org.apache.ibatis.annotations.Mapper;

public interface MasterpieceService {
    int save(MasterpieceSaveRequest dto, int userId);
}
