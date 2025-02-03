package com.dasima.drawrun.global.security;

import java.util.Collection;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Getter
public class UserPrinciple extends User {

  private static final String PASSWORD_ERASED_VALUE = "{PASSWORD_ERASED}"; // Password를 인증할 때만 사용하고 지워버리는 용도이다.
  private final String email;

  public UserPrinciple(String email, String username, Collection<? extends GrantedAuthority> authorities) {
    super(username, PASSWORD_ERASED_VALUE, authorities);
    this.email = email;
  }

  @Override
  public String toString() {
    return "UserPrinciple(" +
        "email= " + email +
        "username=" + getUsername() +
        "role=" + getAuthorities();
  }

}
