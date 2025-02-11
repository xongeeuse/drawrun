package com.dasima.drawrun.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
