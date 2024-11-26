package site.campingon.campingon.common.oauth;

import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import site.campingon.campingon.common.oauth.dto.OAuth2UserDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@ToString
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2UserDto OAuthUserDto;
    private final Map<String, Object> attributes;

    @Override
    public Map<String, Object> getAttributes() {

        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return OAuthUserDto.getRole();
            }
        });

        return collection;
    }

    @Override
    public String getName() {

        return OAuthUserDto.getNickname();
    }

    public String getNickname() {

        return OAuthUserDto.getNickname();
    }

    public String getEmail() {

        return OAuthUserDto.getEmail();
    }

    public String getOauthName(){

        return OAuthUserDto.getOauthName();
    }
}
