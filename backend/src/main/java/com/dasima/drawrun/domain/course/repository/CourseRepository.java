package com.dasima.drawrun.domain.course.repository;

import com.dasima.drawrun.domain.course.entity.Path;
import com.dasima.drawrun.domain.map.entity.User;
import com.dasima.drawrun.domain.mypage.entity.UserPath;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Path, String> {
}
