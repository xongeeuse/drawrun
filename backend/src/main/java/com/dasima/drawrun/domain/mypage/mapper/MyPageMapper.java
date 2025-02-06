package com.dasima.drawrun.domain.mypage.mapper;

import com.dasima.drawrun.domain.mypage.entity.BookMark;
import com.dasima.drawrun.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MyPageMapper {
    public User showinfo(int userId);
    public List<BookMark> bookmark(int userId);
    public BookMark onebookmark(@Param("userId") int userId, @Param("bookmarkId") int bookmarkId);
}
