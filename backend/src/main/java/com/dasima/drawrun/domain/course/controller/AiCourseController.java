package com.dasima.drawrun.domain.course.controller;

import com.dasima.drawrun.domain.course.dto.request.AiCourseMakeRequest;
import com.dasima.drawrun.domain.course.dto.request.FastApiRequest;
import com.dasima.drawrun.domain.course.dto.response.FastApiResponse;
import com.dasima.drawrun.domain.course.service.AiCourseService;
import com.dasima.drawrun.global.common.ApiResponseJson;
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

    @PostMapping("/make")
    public ResponseEntity<ApiResponseJson> makeAiCourse(@RequestBody AiCourseMakeRequest dto) {
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
