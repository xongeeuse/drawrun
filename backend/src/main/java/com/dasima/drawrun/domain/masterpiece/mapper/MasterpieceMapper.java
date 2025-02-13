package com.dasima.drawrun.domain.masterpiece.mapper;

import com.dasima.drawrun.domain.masterpiece.entity.MasterpieceBoard;
import com.dasima.drawrun.domain.masterpiece.entity.MasterpieceSeg;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MasterpieceMapper {
    // 걸작 게시판에 저장
    int save(MasterpieceBoard masterpieceBoard);
    // 걸작 조각에 저장
    int seqsave(MasterpieceSeg masterpieceSeg);

    List<MasterpieceBoard> list();
    MasterpieceBoard search(int masterpieceBoardId);
    List<MasterpieceSeg> pathlist(int masterpieceBoardId);
}
