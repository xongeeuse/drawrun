package com.dasima.drawrun.domain.user.repository;

import com.dasima.drawrun.domain.user.entity.Statistics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatisticsRepository extends JpaRepository<Statistics, Integer> {

    Optional<Statistics> findStatisticsByUserId(int userId);

}
