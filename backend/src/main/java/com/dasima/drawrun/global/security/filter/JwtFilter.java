package com.dasima.drawrun.global.security.filter;

import com.dasima.drawrun.global.security.TokenStatus;
import com.dasima.drawrun.global.security.TokenValidationResult;
import com.dasima.drawrun.global.security.provider.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  public static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String BEARER_REGEX = "Bearer ([a-zA-Z0-9_\\-\\+\\/=]+)\\.([a-zA-Z0-9_\\-\\+\\/=]+)\\.([a-zA-Z0-9_.\\-\\+\\/=]*)"; // 정규 표현식
  private static final Pattern BEARER_PATTERN = Pattern.compile(BEARER_REGEX); // 해당 정규표현식인지 아닌지 확인하는 객체임
  private final TokenProvider tokenProvider;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String token = resolveToken(request); // header의 authorization에 있는 token을 유효한지(token 형식에 맞는지) 검사하고 token을 반환함
    System.out.println(token);
    if(!StringUtils.hasText(token)) { // token이 빈경우 그냥 검증없이 다음필터로 넘어감
      request.setAttribute("result", new TokenValidationResult(TokenStatus.WRONG_AUTH_HEADER, null, null, null)); // WRONG_AUTH_HEADER를 반환해서 토큰의 형식이 틀렸다는걸 알림
      filterChain.doFilter(request, response); // 다음 필터로 넘어감
      return;
    }

    TokenValidationResult tokenValidationResult = tokenProvider.validationToken(token); // 토큰 유효성(서명알고리즘) 검사

    // 유효하지 않다면 넘김
    if(!tokenValidationResult.isValid()) {
      request.setAttribute("result", tokenValidationResult);
      filterChain.doFilter(request, response);
      return;
    }

    // 유효 하다면 Security Context에 Authentication 넣어줌
    // 여기까지 안오면 jwtAccessDeniedHandler에서 요청을 막아버림
    Authentication authentication = tokenProvider.getAuthentication(token, tokenValidationResult.getClaims());
    SecurityContextHolder.getContext().setAuthentication(authentication);
    log.info("AUTH SUCCESS : {}", authentication.getName());
    filterChain.doFilter(request, response);

    // token이 실제로 존재하는 경우

  }

  // 유효한 토큰인지 검사
  // 유효한 토큰이면 인코딩된 jwt토큰을 반환한다.
  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
    if(bearerToken != null && BEARER_PATTERN.matcher(bearerToken).matches()) {
      return bearerToken.substring(7);
    }

    return null;
  }

}
