package com.dasima.drawrun.domain.course.controller;

import com.dasima.drawrun.domain.course.dto.request.BookmarkCancleRequest;
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

    // 코스 리스트 조회(bookmark 수 기준)
    @GetMapping("/list")
    public ResponseEntity<?> list(@AuthenticationPrincipal UserPrinciple userPrinciple,
                                  @RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) String area)
    {

        if(keyword == null && area == null)
            return ResponseEntity.ok(courseService.list(userPrinciple.getUserId(), 1, null));
        else if(keyword != null)
            return ResponseEntity.ok(courseService.list(userPrinciple.getUserId(), 2, keyword));
        else if(area != null)
            return ResponseEntity.ok(courseService.list(userPrinciple.getUserId(), 3, area));
        else return null;
    }
    // 단건 조회
    @GetMapping("search/{userPathId}")
    public ResponseEntity<?> search(@PathVariable int userPathId){
        return ResponseEntity.ok(courseService.search(userPathId));
    }

    // 북마크 저장
    @PostMapping("/bookmark")
    public ResponseEntity<?> bookmark(@AuthenticationPrincipal UserPrinciple userPrinciple, @RequestBody BookmarkCreateRequest dto){
        return ResponseEntity.ok(courseService.bookmark(dto, userPrinciple.getUserId()));
    }

    // 북마크 취소
    @PostMapping("/bookmark/cancle")
    public ResponseEntity<?> bookmarkcancle(@AuthenticationPrincipal UserPrinciple userPrinciple, @RequestBody BookmarkCancleRequest dto){
        return ResponseEntity.ok(courseService.bookmarkcancle(dto, userPrinciple.getUserId()));
    }

}
