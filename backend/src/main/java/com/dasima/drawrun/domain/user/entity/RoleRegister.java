package com.dasima.drawrun.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role_register")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class RoleRegister {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "role_register_id")
  private int roleRegisterId;

  @ManyToOne
  @JoinColumn(name="user_id", referencedColumnName="user_id")
  private User user;

  @ManyToOne
  @JoinColumn(name="role_id", referencedColumnName="role_id")
  private Role role;

}
