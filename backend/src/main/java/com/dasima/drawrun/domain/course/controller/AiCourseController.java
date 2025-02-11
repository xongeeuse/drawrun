package com.dasima.drawrun.domain.course.controller;

import com.dasima.drawrun.domain.course.dto.request.AiCourseMakeRequest;
import com.dasima.drawrun.domain.course.service.AiCourseService;
import com.dasima.drawrun.global.common.ApiResponseJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai")
public class AiCourseController {

    @Autowired
    AiCourseService aiCourseService;

    @PostMapping("/make")
    public ResponseEntity<ApiResponseJson> makeAiCourse(@RequestBody AiCourseMakeRequest dto) {
        // 좌표에 맞는 OSM 파일 가져오기.
        String url = aiCourseService.fetchAndStorePedestrianRoute(dto.getLat(), dto.getLon());

        // OSM 파일과 그림 파일로 분석하기

        return ResponseEntity.ok(
                new ApiResponseJson(true, 200, "성공", Map.of("url", url))
        );
    }

}
