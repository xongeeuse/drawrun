package com.dasima.drawrun.domain.course.service;

import com.dasima.drawrun.domain.course.dto.request.BookmarkCancleRequest;
import com.dasima.drawrun.domain.course.dto.request.BookmarkCreateRequest;
import com.dasima.drawrun.domain.course.dto.request.CourseSaveRequest;
import com.dasima.drawrun.domain.course.dto.response.CourseListResponse;
import com.dasima.drawrun.domain.course.entity.Bookmark;
import com.dasima.drawrun.domain.course.entity.Path;
import com.dasima.drawrun.domain.course.entity.UserPath;
import com.dasima.drawrun.domain.course.mapper.CourseMapper;
import com.dasima.drawrun.domain.course.repository.CourseRepository;
import com.dasima.drawrun.domain.course.vo.KakaoRegionResponse;
import com.dasima.drawrun.domain.course.vo.GeoPoint;

import com.dasima.drawrun.domain.user.entity.User;
import com.dasima.drawrun.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.geo.Point;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.*;

import javax.swing.text.html.Option;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService{
    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @Value("${kakao.api.url}")
    private String kakaoApiUrl;

    private final UserRepository userRepository;

    private final RestTemplate restTemplate;

    private final CourseRepository courseRepository;

    private final CourseMapper courseMapper;
    public KakaoRegionResponse getRegionByCoordinates(double x, double y){
        URI uri = UriComponentsBuilder.fromHttpUrl(kakaoApiUrl)
                .queryParam("x", x)
                .queryParam("y", y)
                .queryParam("input_coord=WGS84")
                .build()
                .toUri();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<KakaoRegionResponse> response = restTemplate.exchange(uri, HttpMethod.GET, entity, KakaoRegionResponse.class);

        return response.getBody();
    }
    //
    public int save(CourseSaveRequest dto, int userId){
        //path의 제일 첫번째 좌표를 가져옴
        GeoPoint standard = dto.getPath().get(0);
        // 지역을 가지고 온다.
        KakaoRegionResponse tmp = getRegionByCoordinates(standard.getLongitude(), standard.getLatitude());
        // 지역을 받아옴
        // 몽고디비 해쉬값 받아옴
        // MongoDB Geojson 저장
        List<GeoPoint> dtoList = dto.getPath();
        List<Point> entityList = dtoList.stream().map(geoPoint -> new Point(geoPoint.getLongitude(), geoPoint.getLatitude())).collect(Collectors.toList());


        Path path = courseRepository.save(new Path(entityList));


        // Dto를 Entity로 바꿔줘야함
        UserPath userPath = UserPath.builder()
                        .address(tmp.getDocuments().get(0).getAddress_name())
                .userId(userId)
                .pathId(path.getId())
                .pathImgUrl(dto.getPathImgUrl())
                .name(dto.getName())
                .build();
        courseMapper.save(userPath);
        return userPath.getUserPathId();
    }
    // 북마크 저장
    public int bookmark(BookmarkCreateRequest dto, int userId){
        // Dto를 Entity로 바꿈
        Bookmark bookmark = Bookmark.builder()
                .userPathId(dto.getUserPathId())
                .userId(userId)
                .build();

        return courseMapper.bookmark(bookmark);
    }

    // 북마크 취소
    public int bookmarkcancle(BookmarkCancleRequest dto, int userId){
        Bookmark bookmark = Bookmark.builder()
                .userPathId(dto.getUserPathId())
                .userId(userId)
                .build();

        return courseMapper.bookmarkcancle(bookmark);
    }

    // 리스트(북마크 기준)
    // 1번 리스트(북마크 기준)
    // 2번
    public List<CourseListResponse> list(int userId, int type, String keywordOrArea){
        List<UserPath> userPaths = null;
        if(type == 1) userPaths = courseMapper.list();
        else if(type == 2) userPaths = courseMapper.keyword(keywordOrArea);
        else userPaths = courseMapper.area(keywordOrArea);
        List<CourseListResponse> courseListResponses = new ArrayList<>();
        for(UserPath userPath : userPaths){
            // username 추출
            User user = userRepository.findById(userPath.getUserId()).orElse(null);

             // 구 정보 추출
             String address = userPath.getAddress();
             int guIndex = address.indexOf("구");
            String gu = null;

            if (guIndex != -1) {
                int start = address.lastIndexOf(" ", guIndex) + 1;
                gu = address.substring(start, guIndex + 1);
            }

            CourseListResponse courseListResponse = CourseListResponse.builder()
                    .userNickname(user.getUserNickname())
                    .userPK(user.getUserId())
                    .profileImgUrl(user.getProfileImgUrl())
                    .courseName(userPath.getName())
                    .location(gu)
                    .isBookmark(courseMapper.isBookmark(userId, userPath.getUserPathId()))
                    .createdAt(userPath.getCreateDate())
                    .distance(userPath.getDistance())
                    .courseImgUrl(userPath.getPathImgUrl())
                    .bookmarkCount(userPath.getBookmarkCount())
                    .build();

            courseListResponses.add(courseListResponse);
        }
        return courseListResponses;
    }
}
