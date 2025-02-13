package com.dasima.drawrun.domain.mypage.repository;

import com.dasima.drawrun.domain.mypage.entity.BadgeInventory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface BadgeInventoryRepository extends JpaRepository<BadgeInventory, Integer> {

    List<BadgeInventory> findBadgeInventoryByUserId(Integer userId);

    Optional<BadgeInventory> findBadgeInventoryByUserIdAndBadgeId(Integer userId, Integer badgeId);

}
