package com.dasima.drawrun.domain.mypage.mapper;

import com.dasima.drawrun.domain.course.entity.Bookmark;
import com.dasima.drawrun.domain.course.entity.UserPath;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MypageMapper {
    List<Bookmark> bookmark(int userId);
}
