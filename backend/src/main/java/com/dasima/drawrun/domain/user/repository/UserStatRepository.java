package com.dasima.drawrun.domain.user.repository;

import com.dasima.drawrun.domain.user.entity.UserStat;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatRepository extends JpaRepository<UserStat, Integer> {

  List<UserStat> findByUserId(int userId);

}
