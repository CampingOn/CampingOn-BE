package site.campingon.campingon.common.config;

public class SecurityPath {

    // permitAll
    public static final String[] PUBLIC_ENDPOINTS = {
        "/api/signup",
        "/api/login",
        "/api/logout",
        "/api/token/refresh",
        "/api/users/check-duplicate",
        "/"
    };


    // hasRole("USER")
    public static final String[] USER_ENDPOINTS = {
        "/api/camps/**",
        "/api/users/me/*",
        "/api/reservations/**",
        "/api/camps/*/bookmarks"
    };

    // hasRole("ADMIN")
    public static final String[] ADMIN_ENDPOINTS = {
        "/api/camps/*/toggle-availability",
        "/api/admin/camps/**",
        "/api/keywords/**",
        "/api/basedList/**",
        "/api/imageList/**"

    };
}
