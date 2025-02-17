package com.dasima.drawrun.domain.user.repository;

import com.dasima.drawrun.domain.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
  Role findByRoleId(int roleId);
}
