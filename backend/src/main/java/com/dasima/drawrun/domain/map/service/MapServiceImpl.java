package com.dasima.drawrun.domain.map.service;

import com.dasima.drawrun.domain.map.dto.PathSaveRequest;
import com.dasima.drawrun.domain.map.entity.Path;
import com.dasima.drawrun.domain.map.entity.User;
import com.dasima.drawrun.domain.map.entity.UserPath;
import com.dasima.drawrun.domain.map.mapper.MapMapper;
import com.dasima.drawrun.domain.map.repository.MapRepository;
import com.dasima.drawrun.domain.map.repository.TestRepository;
import com.dasima.drawrun.domain.map.vo.GeoPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MapServiceImpl implements MapService{
    @Autowired
    MapMapper mapMapper;
    @Autowired
    MapRepository mapRepository;
    @Autowired
    TestRepository testRepository;
    @Override
    public User mongomongotest() {
        User user = new User("donggyu", 30);
        return testRepository.save(user);
    }

    @Override
    public int save(PathSaveRequest dto) {
        // MongoDB Geojson 저장
        // Dto를 Entity로 바꿔줘야함
        List<GeoPoint> dtoList = dto.getPath();
        List<Point> entityList = dtoList.stream().map(geoPoint -> new Point(geoPoint.getLongitude(), geoPoint.getLatitude())).collect(Collectors.toList());

        // MongoDB 에 저장
        Path path = mapRepository.save(new Path(entityList));

        // mysql에 저장
        // UserPath Entity 생성
        UserPath userPath = new UserPath();
        userPath.setPathId(path.getId());
        userPath.setUserId(dto.getUserId());

        return mapMapper.pathsave(userPath);
    }


}
