package com.dasima.drawrun.domain.result.controller;

import com.dasima.drawrun.domain.result.dto.request.CourseResultSaveRequest;
import com.dasima.drawrun.domain.result.service.ResultService;
import com.dasima.drawrun.global.common.ApiResponseJson;
import com.dasima.drawrun.global.exception.CustomException;
import com.dasima.drawrun.global.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/course")
public class ResultController {

  @Autowired
  ResultService courseService;

  @PostMapping("/courseresultsave")
  public ResponseEntity<ApiResponseJson> courseResultSave(@RequestBody CourseResultSaveRequest courseResultSaveRequest, @AuthenticationPrincipal
      UserPrinciple principle) {
    try {
      courseService.courseResultSave(principle.getUserId(), courseResultSaveRequest);

      return ResponseEntity.ok(
          new ApiResponseJson(true, 200, "기록 저장에 성공했습니다.", Map.of("status", true))
      );
    } catch (CustomException e) {
      return ResponseEntity.ok(
          new ApiResponseJson(false, e.getErrorCode().getCode(), e.getMessage(), null)
      );
    }
  }

}
