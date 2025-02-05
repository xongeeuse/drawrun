package com.dasima.drawrun.domain.map.repository;

import com.dasima.drawrun.domain.map.entity.Path;
import com.dasima.drawrun.domain.map.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MapRepository extends MongoRepository<Path, String> {
}
