package com.dasima.drawrun.domain.masterpiece.service;

import com.dasima.drawrun.domain.course.repository.CourseRepository;
import com.dasima.drawrun.domain.course.vo.GeoPoint;
import com.dasima.drawrun.domain.masterpiece.dto.request.MasterpieceSaveRequest;
import com.dasima.drawrun.domain.masterpiece.entity.MasterpieceBoard;
import com.dasima.drawrun.domain.masterpiece.entity.MasterpieceSeg;
import com.dasima.drawrun.domain.masterpiece.mapper.MasterpieceMapper;
import com.dasima.drawrun.domain.course.entity.Path;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MasterpieceServiceImpl implements MasterpieceService{
    private final MasterpieceMapper masterpieceMapper;

    private final CourseRepository courseRepository;
    public int save(MasterpieceSaveRequest dto){
        // entity build
        MasterpieceBoard masterpieceBoard = MasterpieceBoard.builder()
                .userPathId(dto.getUserPathId())
                .restrictCount(dto.getRestrictCount())
                .state(0)
                .build();

        // 걸작 게시판에 저장
        masterpieceMapper.save(masterpieceBoard);
        // 게시판 pk를 받아옴
        int masterpieceBoardId = masterpieceBoard.getMasterpieceBoardId();

        // 경로조각 저장
        List<List<GeoPoint>> dtolist = dto.getPaths();
        // point 로 전환
        // 변환된 리스트
        List<List<Point>> pointList = dtolist.stream()
                .map(innerList -> innerList.stream()
                        .map(geoPoint -> new Point(geoPoint.getLatitude(), geoPoint.getLongitude()))
                        .collect(Collectors.toList()))
                .collect(Collectors.toList());

        int res = 1;
        int pathNum = 0;
        for(List<Point> tmp : pointList){
            // MongoDB에 저장
            Path path = courseRepository.save(new Path(tmp));

            // masterpiece seg에 저장한다.
            res &= masterpieceMapper.seqsave(
                    MasterpieceSeg.builder()
                            .masterpieceBoardId(masterpieceBoardId)
                            .mongoId(path.getId())
                            .pathNum(++pathNum)
                            .build()
            );
        }
        return res;
    }
}
