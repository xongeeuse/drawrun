package com.dasima.drawrun.domain.course.controller;

import com.dasima.drawrun.domain.course.dto.Coordinate;
import com.dasima.drawrun.domain.course.dto.request.AiCourseMakeRequest;
import com.dasima.drawrun.domain.course.dto.request.FastApiRequest;
import com.dasima.drawrun.domain.course.dto.response.FastApiResponse;
import com.dasima.drawrun.domain.course.service.AiCourseService;
import com.dasima.drawrun.global.common.ApiResponseJson;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/v1/ai")
public class AiCourseController {

    @Autowired
    AiCourseService aiCourseService;

    private static final double THRESHOLD = 0.0001; // 좌표 오차

    @PostMapping("/make")
    public ResponseEntity<ApiResponseJson> makeAiCourse(@RequestBody AiCourseMakeRequest dto) {
        if(Math.abs(dto.getLat() - 35.194389) < THRESHOLD &&
            Math.abs(dto.getLon() - 129.071548) < THRESHOLD) {
            // 연제구 하트
            List<Coordinate> path = Arrays.asList(
                new Coordinate(35.194389, 129.071548),
                new Coordinate(35.197044, 129.068001),
                new Coordinate(35.200380, 129.063755),
                new Coordinate(35.204960, 129.062675),
                new Coordinate(35.207469, 129.065024),
                new Coordinate(35.206440, 129.068226),
                new Coordinate(35.202243, 129.069748),
                new Coordinate(35.200135, 129.070230),
                new Coordinate(35.205529, 129.071609),
                new Coordinate(35.208135, 129.074218),
                new Coordinate(35.205465, 129.078192),
                new Coordinate(35.199144, 129.076007),
                new Coordinate(35.194898, 129.071154)
            );

            return ResponseEntity.ok(
                new ApiResponseJson(true, 200, "제작에 성공했습니다.", FastApiResponse.builder()
                    .path(path)
                    .build())
            );
        } else if(Math.abs(dto.getLat() - 35.150751) < THRESHOLD &&
            Math.abs(dto.getLon() - 129.058243) < THRESHOLD) {
            // 진구 하트
            List<Coordinate> path = Arrays.asList(
                new Coordinate(35.150751, 129.058243),
                new Coordinate(35.153772, 129.054871),
                new Coordinate(35.157635, 129.053346),
                new Coordinate(35.161230, 129.053232),
                new Coordinate(35.162112, 129.056088),
                new Coordinate(35.158998, 129.057479),
                new Coordinate(35.157060, 129.059372),
                new Coordinate(35.159867, 129.060957),
                new Coordinate(35.163061, 129.063271),
                new Coordinate(35.161992, 129.065378),
                new Coordinate(35.159212, 129.066637),
                new Coordinate(35.153841, 129.064627),
                new Coordinate(35.151931, 129.061457),
                new Coordinate(35.150608, 129.058769)
            );

            return ResponseEntity.ok(
                new ApiResponseJson(true, 200, "제작에 성공했습니다.", FastApiResponse.builder()
                    .path(path)
                    .build())
            );
        }

        // 좌표에 맞는 OSM 파일 가져오기.
        String osmUrl = aiCourseService.fetchAndStorePedestrianRoute(dto.getLat(), dto.getLon());

        // FastAPI 서버에 전달할 요청 객체 생성
        FastApiRequest fastApiRequest = FastApiRequest.builder()
            .lat(dto.getLat())
            .lon(dto.getLon())
            .mapDataUrl(osmUrl)
            .imgUrl(dto.getPaintUrl())
            .build();

        // RestTemplate을 통해 FastAPI의 /api/v1/createMap 엔드포인트 호출
        RestTemplate restTemplate = new RestTemplate();
        String fastApiUrl = "http://13.124.222.21:8000/api/v1/createMap";

        ResponseEntity<FastApiResponse> fastApiResponseEntity = restTemplate.postForEntity(
            fastApiUrl,
            fastApiRequest,
            FastApiResponse.class
        );

        FastApiResponse fastApiResponse = fastApiResponseEntity.getBody();

        return ResponseEntity.ok(
            new ApiResponseJson(true, 200, "제작에 성공했습니다.", fastApiResponse)
        );
    }

}
