package site.campingon.campingon.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import site.campingon.campingon.common.jwt.CustomAccessDeniedHandler;
import site.campingon.campingon.common.jwt.CustomAuthenticationEntryPoint;
import site.campingon.campingon.common.oauth.handler.CustomOAuth2FailureHandler;
import site.campingon.campingon.common.oauth.service.CustomOAuth2UserService;
import site.campingon.campingon.common.jwt.CustomUserDetailsService;
import site.campingon.campingon.common.jwt.JwtAuthenticationFilter;
import site.campingon.campingon.common.jwt.JwtTokenProvider;
import site.campingon.campingon.common.oauth.handler.CustomOAuthSuccessHandler;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOAuthSuccessHandler customOAuthSuccessHandler;
    private final CustomOAuth2FailureHandler customOAuthFailureHandler;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf((auth) -> auth.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin((auth) -> auth.disable())
                .httpBasic((auth) -> auth.disable())
                .headers((headerConfig) -> headerConfig
                        .frameOptions(frameOptionsConfig -> frameOptionsConfig.disable()));

        // oauth2 로그인 설정
        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
//                        .defaultSuccessUrl("/oauth/success") // 로그인 성공시 이동할 URL
                        .successHandler(customOAuthSuccessHandler)
//                        .failureUrl("/oauth/fail") // 로그인 실패시 이동할 URL
                        .failureHandler(customOAuthFailureHandler))
                .logout(logout -> logout.logoutSuccessUrl("/oauth/logout") // 로그아웃 성공시 해당 url로 이동
                );

        // 경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/","/h2-console/**").permitAll()
                        .anyRequest().permitAll());

        // 예외 처리
        http
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper)) // 인증 실패 처리
                .accessDeniedHandler(new CustomAccessDeniedHandler(objectMapper))); // 인가 실패 처리

        // JwtFilter 추가
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, objectMapper), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 설정을 위한 Bean 등록
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000"); // 허용할 클라이언트 도메인
        configuration.addAllowedMethod("*"); // 모든 HTTP 메서드 허용
        configuration.addAllowedHeader("*"); // 모든 헤더 허용
        configuration.setAllowCredentials(true); // 인증 정보 허용 (쿠키 등)

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 적용
        return source;
    }

    // Authentication manager
    @Bean
    public AuthenticationManager authenticationManager(
        HttpSecurity http,
        PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder
            = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
            .userDetailsService(customUserDetailsService)
            .passwordEncoder(passwordEncoder);

        return authenticationManagerBuilder.build();
    }

    // 비밀번호 암호화 저장을 위한 Encoder Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}