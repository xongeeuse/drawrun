package com.dasima.drawrun.domain.course.service;

import com.dasima.drawrun.domain.course.dto.request.BookmarkCancleRequest;
import com.dasima.drawrun.domain.course.dto.request.BookmarkCreateRequest;
import com.dasima.drawrun.domain.course.dto.request.CourseSaveRequest;
import com.dasima.drawrun.domain.course.dto.response.CourseListResponse;
import com.dasima.drawrun.domain.course.dto.response.CourseResponse;

import java.util.List;

public interface CourseService {
    int save(CourseSaveRequest dto, int userId);
    public int bookmark(BookmarkCreateRequest dto, int userId);
    public int bookmarkcancle(BookmarkCancleRequest dto, int userId);
    public CourseResponse search(int pathUserId);

    public List<CourseListResponse> list(int userId, int type, String keywordOrArea);
}
