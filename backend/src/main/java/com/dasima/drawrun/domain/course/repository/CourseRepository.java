package com.dasima.drawrun.domain.course.repository;

import com.dasima.drawrun.domain.course.entity.Path;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Path, String> {
}
