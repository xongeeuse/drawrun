package com.dasima.drawrun.domain.user.service;

import com.dasima.drawrun.domain.user.dto.request.EmailAuthNumberRequestDto;
import com.dasima.drawrun.domain.user.dto.request.EmailSendRequestDto;
import com.dasima.drawrun.domain.user.dto.request.RegisterRequestDto;
import com.dasima.drawrun.domain.user.entity.Role;
import com.dasima.drawrun.domain.user.entity.RoleRegister;
import com.dasima.drawrun.domain.user.entity.User;
import com.dasima.drawrun.domain.user.repository.RoleRegisterRepository;
import com.dasima.drawrun.domain.user.repository.RoleRepository;
import com.dasima.drawrun.domain.user.repository.UserRepository;
import com.dasima.drawrun.global.exception.CustomException;
import com.dasima.drawrun.global.exception.ErrorCode;
import com.dasima.drawrun.global.security.dto.response.AccessTokenInfoResponseDto;
import com.dasima.drawrun.global.security.dto.response.TokenResponseDto;
import com.dasima.drawrun.global.security.provider.TokenProvider;
import com.dasima.drawrun.global.util.RandomStringGenerator;
import com.dasima.drawrun.global.util.RedisUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final long LIMIT_TIME = 180000; // mail 인증 만료시간
  private final UserRepository userRepository;

  @Value("${jwt.secret}")
  private String secret;
  private final TokenProvider tokenProvider;
  private final UserRepository userRepo;
  private final RoleRegisterRepository roleRegisterRepo;
  private final RoleRepository roleRepo;
  private final BCryptPasswordEncoder passwordEncoder;
  private final RedisUtils redisUtils;
  private final JavaMailSender javaMailSender;

  // 사용할 문자 집합
  private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
  private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
  private static final String DIGIT = "0123456789";
  private static final String SPECIAL_CHARS = "!@#$%^&*()-_=+[]{}|;:,.<>?";

  // 전체 문자 집합
  private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + DIGIT + SPECIAL_CHARS;

  private static final SecureRandom random = new SecureRandom();

  @Override
  public Object register(RegisterRequestDto dto) {

    // 중복 이메일 검사
    if(userRepo.existsByUserEmail(dto.getEmail()))
      throw new CustomException(ErrorCode.DUPLICATE_MEMBER_EMAIL);
    // 중복 아이디 검사
    if(userRepo.existsById(dto.getUserId()))
      throw new CustomException(ErrorCode.DUPLICATE_MEMBER_ID);
    // 중복 닉네임 검사
    if(userRepo.existsByUserNickname(dto.getNickname()))
      throw new CustomException(ErrorCode.DUPLICATE_MEMBER_NICKNAME);

    User user = dto.toUserEntity();

    // password
    user.setUserPassword(passwordEncoder.encode(dto.getPassword()));

    User saveUser = userRepo.save(user);

    Role role = roleRepo.findByRoleId(1); // 1 번 임시사용자.

    // role register
    RoleRegister roleRegister = new RoleRegister(); // 1 번 임시사용자
    roleRegister.setUser(saveUser);
    roleRegister.setRole(role);
    RoleRegister saveRoleRegister = roleRegisterRepo.save(roleRegister);

    return Map.of(
        "status", true
    );
  }

  private User findById(String id) {
    return userRepo.findById(id).orElseThrow(() -> {
      return new CustomException(ErrorCode.NOT_EXIST_MEMBER_ID);
    });
  }

  private User findUserWithRoleNameByUserId(int userId) {
    return userRepo.findUserWithRoleNameById(userId);
  }

  private void checkPassword(String password, User user) {
    if (!passwordEncoder.matches(password, user.getUserPassword())) {
      throw new CustomException(ErrorCode.INCORRECT_MEMBER_PASSWORD);
    }
  }

  @Override
  public TokenResponseDto login(String id, String password) {
    // user 찾기
    User user = findById(id);
    User detailUser = findUserWithRoleNameByUserId(user.getUserId());
    checkPassword(password, detailUser);

    AccessTokenInfoResponseDto accessTokenInfoResponseDto = tokenProvider.createAccessToken(
        detailUser);

    TokenResponseDto tokenResponseDto = new TokenResponseDto();
    tokenResponseDto.setAccessTokenInfoResponse(accessTokenInfoResponseDto);
    tokenResponseDto.setRefreshTokenInfoResponse(
        tokenProvider.createRefreshToken(detailUser, 604800 * 1000));

    // refresh 토큰을 redis에 저장
    redisUtils.setData(id, tokenResponseDto.getRefreshTokenInfoResponse(), (long) 604800 * 1000);
    //refresh 토큰과 access token 두개를 발급한다.
    return tokenResponseDto;
  }

  private User findByEmail(String email) {
    return userRepo.findByUserEmail(email).orElseThrow(() -> {
      return new CustomException(ErrorCode.NOT_EXIST_MEMBER_EMAIL);
    });
  }

  @Override
  public TokenResponseDto reissue(String refreshToken) {
    // 토큰 파싱
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    Key hashKey = Keys.hmacShaKeyFor(keyBytes);
    Claims claims = Jwts.parserBuilder().setSigningKey(hashKey).build().parseClaimsJws(refreshToken)
        .getBody();
    String email = claims.getSubject();
    User user = findByEmail(email);
    User detailUser = findUserWithRoleNameByUserId(user.getUserId());
    String userId = user.getId();

    // redis에 refresh 토큰이 존재 하지 않는다면 그냥 검증할 수 없음
    // 위조, 만료, 전부다 막힘
    if (redisUtils.getData(userId) == null) {
      throw new CustomException(ErrorCode.INVALID_REFRESH_TOKEN);
    }
    else {

      // refresh rotate
      // 토큰 재발급

      // access token 재발급
      AccessTokenInfoResponseDto accessTokenInfoResponseDto = tokenProvider.createAccessToken(detailUser);

      // refreshToken의 남은 시간(초)를 계산
      Date expirationDate = claims.getExpiration();
      long currentTimeMillis = System.currentTimeMillis();
      long remainingTimeMillis = expirationDate.getTime() - currentTimeMillis;
      long remainingSeconds = remainingTimeMillis / 1000;

      // refresh token 발급
      TokenResponseDto tokenResponseDto = new TokenResponseDto();
      tokenResponseDto.setRefreshExpireTime(remainingSeconds);
      tokenResponseDto.setAccessTokenInfoResponse(accessTokenInfoResponseDto);
      tokenResponseDto.setRefreshTokenInfoResponse(tokenProvider.createRefreshToken(detailUser, remainingSeconds * 1000));


      // 기존의 refresh token을 삭제
      redisUtils.deleteData(userId);
      redisUtils.setData(userId, tokenResponseDto.getRefreshTokenInfoResponse(),
          remainingSeconds * 1000);
      return tokenResponseDto;
    }
  }

  @Override
  public void logout(String accessToken) {
    byte[] keyBytes = Decoders.BASE64.decode(secret);
    Key hashKey = Keys.hmacShaKeyFor(keyBytes);
    Claims claims = Jwts.parserBuilder()
        .setSigningKey(hashKey)
        .build()
        .parseClaimsJws(accessToken.substring(6))
        .getBody();
    String email = claims.getSubject();
    User user = findByEmail(email);

    redisUtils.deleteData(user.getId());
  }

  @Override
  public void sendmail(EmailSendRequestDto dto) {
    if (redisUtils.getData(dto.getEmail()) != null)
      redisUtils.deleteData(dto.getEmail());

    String authNumber = RandomStringGenerator.generateRandomNumber(); // 6자리수 생성
    redisUtils.setData(dto.getEmail(), authNumber, LIMIT_TIME);

    try {
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
      messageHelper.setSubject("이메일 주소 확인");
      messageHelper.setTo(dto.getEmail());
      messageHelper.setText(authNumber);
      javaMailSender.send(mimeMessage);
    } catch (Exception e) {
      throw new CustomException(ErrorCode.FAIL_EMAIL_SEND);
    }
  }

  @Override
  public void mailcheck(EmailAuthNumberRequestDto dto) {
    String authNumber = redisUtils.getData(dto.getEmail());

    log.info(authNumber);

    if (authNumber.equals(dto.getAuthNumber()))
      return;
    throw new CustomException(ErrorCode.FAIL_EMAIL_AUTH);
  }

  public String generateRandomPassword() {
    StringBuilder sb = new StringBuilder(15);
    for (int i = 0; i < 15; i++) {
      int rndCharAt = random.nextInt(PASSWORD_ALLOW_BASE.length());
      char rndChar = PASSWORD_ALLOW_BASE.charAt(rndCharAt);
      sb.append(rndChar);
    }
    return sb.toString();
  }

  @Override
  public void findPassword(String userId, String email) {
    // 1. 사용자 조회 (예: Optional을 반환하는 메서드를 사용)
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER_ID));

    // 2. 입력받은 이메일과 DB의 이메일이 일치하는지 확인
    if (!user.getUserEmail().equalsIgnoreCase(email)) {
      throw new CustomException(ErrorCode.INCORRECT_EMAIL);
    }

    // 3. 임시 비밀번호 생성
    String tempPassword = generateRandomPassword();

    // 4. 임시 비밀번호를 이메일로 전송
    try {
      MimeMessage mimeMessage = javaMailSender.createMimeMessage();
      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
      messageHelper.setSubject("임시 비밀번호 안내");
      messageHelper.setTo(email);
      messageHelper.setText("임시 비밀번호는 " + tempPassword + " 입니다. 로그인 후 반드시 변경해 주세요.", true);
      javaMailSender.send(mimeMessage);
    } catch (Exception e) {
      throw new CustomException(ErrorCode.FAIL_EMAIL_SEND);
    }

    // 5. 임시 비밀번호를 암호화하여 DB에 저장
    String encodedPassword = passwordEncoder.encode(tempPassword);
    user.setUserPassword(encodedPassword);
    userRepository.save(user);
  }

  @Override
  public void changePassword(int userPK, String oldPassword, String newPassword) {
    // 1. userPK로 사용자 조회
    User user = userRepository.findById(userPK)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER_ID));

    // 2. 기존 비밀번호(oldPassword)와 DB 저장 암호화 비밀번호 비교
    if (!passwordEncoder.matches(oldPassword, user.getUserPassword())) {
      throw new CustomException(ErrorCode.PASSWORD_MISMATCH);
    }

    // 3. newPassword를 암호화하여 DB 업데이트
    String encodedNewPassword = passwordEncoder.encode(newPassword);
    user.setUserPassword(encodedNewPassword);
    userRepository.save(user);
  }

  @Override
  public User findId(String email, String username) {
    return userRepository.findByUserEmailAndUserName(email, username)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_MEMBER_EMAIL));
  }

  @Override
  public boolean checkId(String userId) {
    return userRepository.findById(userId).isPresent();
  }

  @Override
  public void withdrawAccount(int userPK) {
    userRepository.deleteUserByUserId(userPK);
  }

}
