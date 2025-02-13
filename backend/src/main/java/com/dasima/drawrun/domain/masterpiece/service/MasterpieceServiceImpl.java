package com.dasima.drawrun.domain.masterpiece.service;

import com.dasima.drawrun.domain.course.repository.CourseRepository;
import com.dasima.drawrun.domain.course.vo.GeoPoint;
import com.dasima.drawrun.domain.course.vo.KakaoRegionResponse;
import com.dasima.drawrun.domain.masterpiece.dto.request.MasterpieceSaveRequest;
import com.dasima.drawrun.domain.masterpiece.dto.response.MasterpieceListResponse;
import com.dasima.drawrun.domain.masterpiece.entity.MasterpieceBoard;
import com.dasima.drawrun.domain.masterpiece.entity.MasterpieceSeg;
import com.dasima.drawrun.domain.masterpiece.mapper.MasterpieceMapper;
import com.dasima.drawrun.domain.course.entity.Path;
import com.dasima.drawrun.domain.user.entity.User;
import com.dasima.drawrun.domain.user.repository.UserRepository;
import com.dasima.drawrun.global.util.KakaoAddressGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MasterpieceServiceImpl implements MasterpieceService{
    private final MasterpieceMapper masterpieceMapper;

    private final CourseRepository courseRepository;

    private final UserRepository userRepository;

    private final KakaoAddressGenerator kakaoAddressGenerator;
    public int save(MasterpieceSaveRequest dto, int userId){
        // entity build
        MasterpieceBoard masterpieceBoard = MasterpieceBoard.builder()
                .userId(userId)
                .userPathId(dto.getUserPathId())
                .restrictCount(dto.getRestrictCount())
                .expireDate(dto.getExpireDateAsLocalDateTime())
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
            // 주소를 구해줘야 함
            Point point = tmp.get(0);
            KakaoRegionResponse kakaoRegionResponse = kakaoAddressGenerator.getRegionByCoordinates(point.getY(), point.getX());

            // MongoDB에 저장
            Path path = courseRepository.save(new Path(tmp));

            // masterpiece seg에 저장한다.
            res &= masterpieceMapper.seqsave(
                    MasterpieceSeg.builder()
                            .masterpieceBoardId(masterpieceBoardId)
                            .mongoId(path.getId())
                            .pathNum(++pathNum)
                            .address(kakaoRegionResponse.getDocuments().get(0).getAddress_name())
                            .build()
            );
        }
        return res;
    }

    public List<MasterpieceListResponse> list(){
        List<MasterpieceListResponse> masterpieceListResponses = new ArrayList<MasterpieceListResponse>();
        List<MasterpieceBoard> masterpieceBoards = masterpieceMapper.list();

        for(MasterpieceBoard masterpieceBoard : masterpieceBoards){
            LocalDateTime createDate = masterpieceBoard.getExpireDate();
            LocalDateTime expireDate = masterpieceBoard.getCreateDate();

            // 구정보 추출
            String address = masterpieceBoard.getUserPath().getAddress();
            int guIndex = address.indexOf("구");
            String gu = null;

            if (guIndex != -1) {
                int start = address.lastIndexOf(" ", guIndex) + 1;
                gu = address.substring(start, guIndex + 1);
            }

            User user = userRepository.findById(masterpieceBoard.getUserId()).orElse(null);

            // list 저장
            masterpieceListResponses.add(
                    MasterpieceListResponse.builder()
                            .dDay((int) ChronoUnit.DAYS.between(expireDate.toLocalDate(), createDate.toLocalDate()))
                            .gu(gu)
                            .distance(masterpieceBoard.getUserPath().getDistance())
                            .pathImgUrl(masterpieceBoard.getUserPath().getPathImgUrl())
                            .profileImgUrl(user.getProfileImgUrl())
                            .nickname(user.getUserNickname())
                            .userPathId(masterpieceBoard.getUserPath().getUserPathId())
                            .restrictCount(masterpieceBoard.getRestrictCount())
                            .userId(masterpieceBoard.getUserId())
                            .masterpieceBoardId(masterpieceBoard.getMasterpieceBoardId())
                            .build()
            );
        }
        return masterpieceListResponses;
    }
}
