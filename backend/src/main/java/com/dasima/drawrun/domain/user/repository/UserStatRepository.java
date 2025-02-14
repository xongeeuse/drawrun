package com.dasima.drawrun.domain.user.repository;

import com.dasima.drawrun.domain.user.entity.UserStat;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStatRepository extends JpaRepository<UserStat, Integer> {

  List<UserStat> findByUserIdOrderByDateDesc(int userId);
  Optional<UserStat> findFirstByUserIdOrderByDateDesc(int userId);

}
