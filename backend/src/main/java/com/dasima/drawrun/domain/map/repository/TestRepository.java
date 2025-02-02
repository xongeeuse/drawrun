package com.dasima.drawrun.domain.map.repository;

import com.dasima.drawrun.domain.map.entity.Path;
import com.dasima.drawrun.domain.map.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TestRepository extends MongoRepository<User, String> {
}
