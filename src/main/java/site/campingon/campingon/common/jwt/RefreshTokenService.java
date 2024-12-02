package site.campingon.campingon.common.jwt;


import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveOrUpdateRefreshToken(String email, String refreshToken, long refreshTokenExpired) {
        refreshTokenRepository.findByEmail(email)
            .ifPresentOrElse(
                // 기존 토큰이 존재하면 업데이트
                existingToken -> existingToken.update(refreshToken,
                    LocalDateTime.now().plusSeconds(refreshTokenExpired)),
                // 기존 토큰이 없으면 새로 생성
                () -> refreshTokenRepository.save(RefreshToken.builder()
                    .email(email)
                    .token(refreshToken)
                    .exp(LocalDateTime.now().plusSeconds(refreshTokenExpired))
                    .build())
            );
    }

    public Optional<RefreshToken> getRefreshTokenByEmail(String email) {
        return refreshTokenRepository.findByEmail(email);
    }

    public Optional<RefreshToken> getRefreshTokenByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    @Transactional
    public void deleteRefreshTokenByEmail(String email) {
        refreshTokenRepository.deleteByEmail(email);
    }

}
