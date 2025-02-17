package com.dasima.drawrun.domain.mypage.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "badge_inventory")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class BadgeInventory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "badge_inventory_id")
    private Integer badgeInventoryId;
    private Integer userId;
    private Integer badgeId;
    @CreationTimestamp
    @Column(name = "collected_date")
    private LocalDateTime collectedDate;
}
