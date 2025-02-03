package com.dasima.drawrun.global.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jwt") // 알아서 jwt에 있는것과 매핑시켜줌
public class JwtProperties {

  private String header;
  private String secret;
  private Long accessTokenValidityInSeconds;
  private Long refreshTokenValidityInSeconds;

}
