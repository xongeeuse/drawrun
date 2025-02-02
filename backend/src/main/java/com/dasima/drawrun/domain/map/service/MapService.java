package com.dasima.drawrun.domain.map.service;

import com.dasima.drawrun.domain.map.dto.PathSaveRequest;
import com.dasima.drawrun.domain.map.entity.Path;
import com.dasima.drawrun.domain.map.entity.User;
import org.springframework.stereotype.Service;

public interface MapService{
    User mongomongotest();
    int save(PathSaveRequest dto); // 경로저장

}
