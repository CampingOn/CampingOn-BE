package site.campingon.campingon.common.jwt;

import java.util.Map;

public interface CustomUserPrincipal {
    String getEmail();
    String getNickname();
    String getRole();
    String getName();
    Map<String, Object> getAttributes();

}
