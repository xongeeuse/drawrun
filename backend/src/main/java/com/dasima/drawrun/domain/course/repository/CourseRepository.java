package com.dasima.drawrun.domain.course.repository;

import com.dasima.drawrun.domain.course.entity.CourseResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<CourseResult, Integer> {

}
