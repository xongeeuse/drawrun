package com.dasima.drawrun.domain.masterpiece.mapper;

import com.dasima.drawrun.domain.masterpiece.dto.request.MasterpieceJoinRequest;
import com.dasima.drawrun.domain.masterpiece.entity.MasterpieceBoard;
import com.dasima.drawrun.domain.masterpiece.entity.MasterpieceParticipant;
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

    MasterpieceParticipant searchparticipant(int masterpieceSegId);
    int join(MasterpieceParticipant masterpieceParticipant);
    int complete(int masterpieceSegId);
    List<MasterpieceSeg> check(int masterpieceBoardId);
    int updatestate(int masterpieceBoardId);
    MasterpieceSeg returnpk(int masterpieceSegId);

    // 자기가 완료한 걸작 return
    List<MasterpieceBoard> completelist(int userId);
}
