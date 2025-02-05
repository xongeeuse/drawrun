package com.dasima.drawrun.domain.map.mapper;

import com.dasima.drawrun.domain.map.entity.UserPath;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MapMapper {
    public int pathsave(UserPath userPath);
}
