package com.dasima.drawrun.domain.course.controller;

import com.dasima.drawrun.domain.course.dto.CourseSaveRequest;
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


    @PostMapping("/save")
    public ResponseEntity<?> save(@RequestBody CourseSaveRequest dto){
        return ResponseEntity.ok();
    }
}
