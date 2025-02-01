package com.dasima.drawrun.domain.map.service;

import com.dasima.drawrun.domain.map.entity.User;
import com.dasima.drawrun.domain.map.repository.MapRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MapServiceImpl implements MapService{

    @Autowired
    MapRepository mapRepository;
    @Override
    public User mongomongotest() {
        User user = new User("donggyu", 30);
        return mapRepository.save(user);
    }
}
