package com.dasima.drawrun.domain.course.controller;

import com.dasima.drawrun.domain.course.dto.request.CourseResultSaveRequest;
import com.dasima.drawrun.domain.course.service.CourseService;
import com.dasima.drawrun.global.common.ApiResponseJson;
import com.dasima.drawrun.global.exception.CustomException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/course")
public class CourseController {

  @Autowired
  CourseService courseService;

  @PostMapping("/courseresultsave")
  public ResponseEntity<ApiResponseJson> courseResultSave(@RequestBody CourseResultSaveRequest courseResultSaveRequest) {
    try {
      courseService.courseResultSave(courseResultSaveRequest);

      return ResponseEntity.ok(
          new ApiResponseJson(true, 200, "기록 저장에 성공했습니다.", Map.of("status", true))
      );
    } catch (CustomException e) {
      return ResponseEntity.ok(
          new ApiResponseJson(false, e.getErrorCode().getCode(), e.getMessage(), null);
      )
    }
  }

}
