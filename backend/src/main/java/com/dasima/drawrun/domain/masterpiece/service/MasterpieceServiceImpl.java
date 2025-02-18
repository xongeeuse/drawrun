package com.dasima.drawrun.domain.masterpiece.service;

import com.dasima.drawrun.domain.course.repository.CourseRepository;
import com.dasima.drawrun.domain.course.vo.GeoPoint;
import com.dasima.drawrun.domain.course.vo.KakaoRegionResponse;
import com.dasima.drawrun.domain.course.vo.KakaoRoadAddressResponse;
import com.dasima.drawrun.domain.masterpiece.dto.request.MasterpieceJoinRequest;
import com.dasima.drawrun.domain.masterpiece.dto.request.MasterpieceSaveRequest;
import com.dasima.drawrun.domain.masterpiece.dto.response.MasterpieceListResponse;
import com.dasima.drawrun.domain.masterpiece.dto.response.PathListResponse;
import com.dasima.drawrun.domain.masterpiece.entity.MasterpieceBoard;
import com.dasima.drawrun.domain.masterpiece.entity.MasterpieceParticipant;
import com.dasima.drawrun.domain.masterpiece.entity.MasterpieceSeg;
import com.dasima.drawrun.domain.masterpiece.mapper.MasterpieceMapper;
import com.dasima.drawrun.domain.course.entity.Path;
import com.dasima.drawrun.domain.user.entity.User;
import com.dasima.drawrun.domain.user.repository.UserRepository;
import com.dasima.drawrun.global.util.KakaoAddressGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.AbstractList;
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
            KakaoRoadAddressResponse kakaoRoadAddressResponse = kakaoAddressGenerator.getRoadAddressByCoordinates(point.getY(), point.getX());

            // MongoDB에 저장
            Path path = courseRepository.save(new Path(tmp));

            // masterpiece seg에 저장한다.
            res &= masterpieceMapper.seqsave(
                    MasterpieceSeg.builder()
                            .masterpieceBoardId(masterpieceBoardId)
                            .mongoId(path.getId())
                            .pathNum(++pathNum)
                            .address(kakaoRegionResponse.getDocuments().get(0).getAddress_name())
                            .address2(kakaoRoadAddressResponse.getDocuments().get(0).getRoadAddress().getAddressName())
                            .build()
            );
        }
        if(res == 1) return masterpieceBoard.getMasterpieceBoardId();
        else return 0;
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
                            .courseName(masterpieceBoard.getUserPath().getName())
                            .joinCount(masterpieceBoard.getParticipantCount())
                            .state(masterpieceBoard.getState())
                            .build()
            );
        }
        return masterpieceListResponses;
    }

    public MasterpieceListResponse search(int masterpieceBoardId){
        MasterpieceBoard masterpieceBoard = masterpieceMapper.search(masterpieceBoardId);
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

        return
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
                        .courseName(masterpieceBoard.getUserPath().getName())
                .masterpieceBoardId(masterpieceBoard.getMasterpieceBoardId())
                        .joinCount(masterpieceBoard.getParticipantCount())
                        .state(masterpieceBoard.getState())
                .build();
    }

    public List<PathListResponse> pathlist(int masterpieceBoardId){
        List<MasterpieceSeg> masterpieceSegs = masterpieceMapper.pathlist(masterpieceBoardId);
        List<PathListResponse> listResponses = new ArrayList<PathListResponse>();
        for(MasterpieceSeg masterpieceSeg : masterpieceSegs){
            // mongo에서 geo json을 가져옴
            Path path = courseRepository.findById(masterpieceSeg.getMongoId()).orElse(null);
            GeoJsonLineString geoJsonLineString = path.getPath();
            List<Point> list = geoJsonLineString.getCoordinates();

            // geoPoints로 변환
            List<GeoPoint> geoPoints = new ArrayList<GeoPoint>(); // 경로

            for(Point point : list){
                GeoPoint tmp = new GeoPoint(point.getX(), point.getY());
                geoPoints.add(tmp);
            }

            // 참가자 조회
            MasterpieceParticipant masterpieceParticipant = masterpieceMapper.searchparticipant(masterpieceSeg.getMasterpieceSegId());

            String nicknameOrState = null; // 경로의 상태

            // 참가자가 없다는 뜻
            if(masterpieceParticipant == null) nicknameOrState = "달리기 시작";
            else if(masterpieceParticipant.getState() == 1){
                User user = userRepository.findById(masterpieceParticipant.getUserId()).orElse(null);
                nicknameOrState = user.getUserNickname();
            } else if(masterpieceParticipant.getState() == 0)
                nicknameOrState = "달리는 중";

            // PathListResponse build
            listResponses.add(
                    PathListResponse.builder()
                            .path(geoPoints)
                            .masterpieceSegId(masterpieceSeg.getMasterpieceSegId())
                            .nickname(nicknameOrState)
                            .address(masterpieceSeg.getAddress())
                            .build()
            );
        }
        return listResponses;
    }

    public int join(MasterpieceJoinRequest masterpieceJoinRequest, int userId){
        return masterpieceMapper.join(
                MasterpieceParticipant.builder()
                        .masterpieceSegId(masterpieceJoinRequest.getMasterpieceSegId())
                        .state(0)
                        .userId(userId)
                        .build()
        );
    }

    public int complete(int masterpieceSegId){
        // 일단 무조건 갱신해야됨 participant 테이블의 state를 갱신해줘야 됨
        masterpieceMapper.complete(masterpieceSegId);
        MasterpieceSeg masterpieceSegTmp = masterpieceMapper.returnpk(masterpieceSegId);
        int masterpieceBoardId = masterpieceSegTmp.getMasterpieceBoardId();

        List<MasterpieceSeg> masterpieceSegs =  masterpieceMapper.check(masterpieceBoardId);
        int result = 1;
        for(MasterpieceSeg masterpieceSeg : masterpieceSegs){
            // state 가 하나라도 0 이라면 result는 0이다.
            result &= masterpieceSeg.getMasterpieceParticipant().getState();
        }

        // 모든 참가자가 완주했다면
        //masterpiece board에 state를 1을 올려준다.
        if(result == 1)
            return masterpieceMapper.updatestate(masterpieceBoardId);
            // 아직 완주 못했다면
        else
            return 0;
    }

    public List<MasterpieceListResponse> completelist(int userId){
        List<MasterpieceListResponse> masterpieceListResponses = new ArrayList<MasterpieceListResponse>();
        List<MasterpieceBoard> masterpieceBoards = masterpieceMapper.completelist(userId);

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
                            .courseName(masterpieceBoard.getUserPath().getName())
                            .joinCount(masterpieceBoard.getParticipantCount())
                            .state(masterpieceBoard.getState())
                            .build()
            );
        }
        return masterpieceListResponses;
    }


}
