package site.campingon.campingon.user.service;

import io.jsonwebtoken.ExpiredJwtException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.campingon.campingon.common.exception.ErrorCode;
import site.campingon.campingon.common.exception.GlobalException;
import site.campingon.campingon.common.jwt.CustomUserDetails;
import site.campingon.campingon.common.jwt.JwtToken;
import site.campingon.campingon.common.jwt.JwtTokenProvider;
import site.campingon.campingon.common.jwt.RefreshToken;
import site.campingon.campingon.common.jwt.RefreshTokenService;
import site.campingon.campingon.user.dto.UserSignInRequestDto;
import site.campingon.campingon.user.entity.User;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAuthService {

    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();


    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    @Value("${jwt.refresh-expired}")
    private Long refreshTokenExpired;

    // 로그인
    @Transactional
    public JwtToken login(UserSignInRequestDto signInRequestDto) {
        User user = validateUser(signInRequestDto);
        Authentication authentication = authenticateUser(user.getEmail(), signInRequestDto.getPassword());
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);

        refreshTokenService.saveOrUpdateRefreshToken(user.getEmail(), jwtToken.getRefreshToken(), refreshTokenExpired);
        return jwtToken;
    }

    // 토큰 재발급
    @Transactional
    public JwtToken refresh(String refreshToken) {
        ReentrantLock lock = locks.computeIfAbsent(refreshToken, key -> new ReentrantLock());
        lock.lock();

        try{
            log.info("Refresh Token을 사용한 Access Token 재발급");

            RefreshToken storedRefreshToken = refreshTokenService.getRefreshTokenByToken(refreshToken)
                .orElseThrow(() -> new GlobalException(ErrorCode.NO_TOKEN));

            if (storedRefreshToken.getExp().isBefore(LocalDateTime.now())) {
                throw new GlobalException(ErrorCode.REFRESH_TOKEN_EXPIRED);
            }

            User user = userService.findUserByEmail(storedRefreshToken.getEmail());
            Authentication authentication = createAuthentication(user);

            return jwtTokenProvider.generateToken(authentication);

        } finally {
            lock.unlock();
            locks.remove(refreshToken);
        }

    }

    // 로그 아웃
    @Transactional
    public void logout(String accessToken) {
        try {
            String email = jwtTokenProvider.getEmailFromToken(accessToken);
            refreshTokenService.deleteRefreshTokenByEmail(email);
        } catch (ExpiredJwtException e) {
            refreshTokenService.deleteRefreshTokenByEmail(e.getClaims().getSubject());
        } catch (Exception e) {
            throw new RuntimeException("로그아웃 중 문제가 발생했습니다: " + e.getMessage());
        }
    }

    // email, password를 사용해서 유저 확인
    private User validateUser(UserSignInRequestDto signInRequestDto) {
        User user = userService.findUserByEmail(signInRequestDto.getEmail());

        if (!passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())) {
            throw new GlobalException(ErrorCode.INVALID_PASSWORD);
        }
        return user;
    }

    // 이메일, authentication 생성
    private Authentication authenticateUser(String email, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(email, password);

        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }


    // User 객체를 사용해 authentication 생성
    private Authentication createAuthentication(User user) {
        CustomUserDetails customUserDetails = new CustomUserDetails(
            user.getId(), user.getEmail(), user.getNickname(), user.getRole(), user.getPassword(),user.getName());

        return new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
    }

}