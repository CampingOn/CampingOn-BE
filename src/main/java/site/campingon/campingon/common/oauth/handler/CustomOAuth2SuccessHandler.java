package site.campingon.campingon.common.oauth.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import site.campingon.campingon.common.jwt.JwtToken;
import site.campingon.campingon.common.jwt.JwtTokenProvider;
import site.campingon.campingon.common.jwt.RefreshTokenService;
import site.campingon.campingon.common.oauth.CustomOAuth2User;
import site.campingon.campingon.common.util.CookieUtil;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.front-url}")
    private String frontUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // JWT 토큰 생성
        JwtToken jwtToken = jwtTokenProvider.generateToken(authentication);
        String refreshToken = jwtToken.getRefreshToken();
        String email = ((CustomOAuth2User) authentication.getPrincipal()).getEmail();

        // Refresh Token을 쿠키에 추가
        CookieUtil.setCookie(response, "refreshToken", refreshToken, jwtTokenProvider.getRefreshTokenExpired());

        // Refresh Token을 DB에 저장
        refreshTokenService.saveOrUpdateRefreshToken(email, refreshToken, jwtTokenProvider.getRefreshTokenExpired());


        String redirectUrl = frontUrl + "/oauth/success";
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
