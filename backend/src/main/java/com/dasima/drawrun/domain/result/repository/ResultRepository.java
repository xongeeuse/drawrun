package com.dasima.drawrun.domain.result.repository;

import com.dasima.drawrun.domain.result.entity.CourseResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultRepository extends JpaRepository<CourseResult, Integer> {

}
