package com.dasima.drawrun.domain.course.mapper;

import com.dasima.drawrun.domain.course.entity.Bookmark;
import com.dasima.drawrun.domain.course.entity.Path;
import com.dasima.drawrun.domain.course.entity.UserPath;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMapper {
    int save(UserPath userPath);
    int bookmark(Bookmark bookmark);

    int bookmarkcancle(Bookmark bookmark);
    UserPath list();
}
