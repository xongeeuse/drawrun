package com.dasima.drawrun.global.config;

import com.dasima.drawrun.global.security.JwtAccessDeniedHandler;
import com.dasima.drawrun.global.security.JwtAuthenticationEntryPoint;
import com.dasima.drawrun.global.security.provider.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

  @Bean
  public TokenProvider tokenProvider(JwtProperties jwtProperties) {
    return new TokenProvider(jwtProperties.getSecret(), jwtProperties.getAccessTokenValidityInSeconds(), jwtProperties.getRefreshTokenValidityInSeconds());
  }

  @Bean
  public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
    return new JwtAuthenticationEntryPoint();
  }

  @Bean
  public JwtAccessDeniedHandler jwtAccessDeniedHandler() {
    return new JwtAccessDeniedHandler();
  }

}
