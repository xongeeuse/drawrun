package com.dasima.drawrun.domain.course.mapper;

import com.dasima.drawrun.domain.course.entity.Bookmark;
import com.dasima.drawrun.domain.course.entity.Path;
import com.dasima.drawrun.domain.course.entity.UserPath;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CourseMapper {
    int save(UserPath userPath);
    int bookmark(Bookmark bookmark);

    UserPath search(int userPathId);
    int bookmarkcancle(Bookmark bookmark);
    List<UserPath> list();
    Boolean isBookmark(@Param("userId") int userId, @Param("userPathId") int userPathId);
    List<UserPath> keyword(String keyword);
    List<UserPath> area(String area);
}
