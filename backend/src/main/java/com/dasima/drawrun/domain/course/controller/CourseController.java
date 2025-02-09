package com.dasima.drawrun.domain.course.controller;

import com.dasima.drawrun.domain.course.dto.request.BookmarkCreateRequest;
import com.dasima.drawrun.domain.course.dto.request.CourseSaveRequest;
import com.dasima.drawrun.domain.course.service.CourseService;
import com.dasima.drawrun.global.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/course")
public class CourseController {
    @Autowired
    CourseService courseService;

    // 코스 저장
    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody CourseSaveRequest dto, @AuthenticationPrincipal UserPrinciple userPrinciple){
        return ResponseEntity.ok(courseService.save(dto, userPrinciple.getUserId()));
    }

    // 북마크 저장
    @PostMapping("/bookmark")
    public ResponseEntity<?> bookmark(@AuthenticationPrincipal UserPrinciple userPrinciple, @RequestBody BookmarkCreateRequest dto){
        return ResponseEntity.ok(courseService.bookmark(dto, userPrinciple.getUserId()));

    }
}
