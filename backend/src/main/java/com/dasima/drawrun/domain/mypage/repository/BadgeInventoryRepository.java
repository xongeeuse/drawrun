package com.dasima.drawrun.domain.mypage.repository;

import com.dasima.drawrun.domain.mypage.entity.BadgeInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BadgeInventoryRepository extends JpaRepository<BadgeInventory, Integer> {

    List<BadgeInventory> findBadgeInventoryByUserId(Integer userId);
    
}
