package com.dasima.drawrun.domain.user.repository;

import com.dasima.drawrun.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

  Optional<User> findById(String id);
  Optional<User> findByUserId(int id);
  Optional<User> findByUserEmail(String email);

  Optional<User> findByUserEmailAndUserName(String email, String name);

  boolean existsById(String id);

  boolean existsByUserNickname(String nickname);

  boolean existsByUserEmail(String email);

  void deleteUserByUserId(int userId);


  @Query("SELECT u FROM User u " +
      "LEFT JOIN FETCH u.roleRegister rr " +
      "LEFT JOIN FETCH rr.role r " +
      "WHERE u.userId = :userId")
  User findUserWithRoleNameById(@Param("userId") int userId);

}
