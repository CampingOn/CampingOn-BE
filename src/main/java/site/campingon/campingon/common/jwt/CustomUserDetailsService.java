package site.campingon.campingon.common.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import site.campingon.campingon.user.entity.User;
import site.campingon.campingon.user.repository.UserRepository;

@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // DB에서 사용자 조회
        User user = userRepository.findByEmailAndDeletedAtIsNull(email)
            .orElseThrow(() -> new UsernameNotFoundException("해당하는 정보의 사용자를 찾을 수 없습니다."));

        // CustomUserDetails 생성
        return new CustomUserDetails(
            user.getId(), user.getEmail(), user.getNickname(), user.getRole(), user.getPassword(), user.getName()
        );
    }
}