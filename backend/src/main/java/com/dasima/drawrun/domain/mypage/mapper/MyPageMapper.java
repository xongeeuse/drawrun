package com.dasima.drawrun.domain.mypage.mapper;

import com.dasima.drawrun.domain.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MyPageMapper {
    public User showinfo(int userId);
}
