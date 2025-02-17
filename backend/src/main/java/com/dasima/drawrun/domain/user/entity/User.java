package com.dasima.drawrun.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Integer userId;
  private String id;
  private String userEmail;
  private String userName;
  private String userNickname;
  private String userPassword;
  @Column(name = "profile_img_url")
  private String profileImgUrl;
  private String socialType;
  private String socialId;
  private Integer badgeId;
  @CreationTimestamp
  @Column(name = "create_date")
  private LocalDateTime createDate;
  @UpdateTimestamp
  @Column(name = "update_date")
  private LocalDateTime updateDate;
  private Boolean isDeleted;
  private String region;

  @OneToMany(mappedBy = "user")
  private List<RoleRegister> roleRegister;

//  public ShowInfoResponse toShowInfoResponseDto(){
//    return ShowInfoResponse
//            .builder()
//            .nickname(userName)
//            .profileImgUrl(profileImgUrl)
//            .userId(userId)
//            .build();
//  }

}
