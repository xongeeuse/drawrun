package com.dasima.drawrun.domain.masterpiece.service;

import com.dasima.drawrun.domain.masterpiece.dto.request.MasterpieceSaveRequest;
import com.dasima.drawrun.domain.masterpiece.dto.response.MasterpieceListResponse;
import com.dasima.drawrun.domain.masterpiece.dto.response.PathListResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

public interface MasterpieceService {
    int save(MasterpieceSaveRequest dto, int userId);
    List<MasterpieceListResponse> list();
    public MasterpieceListResponse search(int masterpieceBoardId);
    public List<PathListResponse> pathlist(int masterpieceBoardId);
}
