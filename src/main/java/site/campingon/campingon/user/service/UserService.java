package site.campingon.campingon.user.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.campingon.campingon.user.dto.UserDeactivateRequestDto;
import site.campingon.campingon.user.dto.UserResponseDto;
import site.campingon.campingon.user.dto.UserSignUpRequestDto;
import site.campingon.campingon.user.dto.UserSignUpResponseDto;
import site.campingon.campingon.user.dto.UserUpdateRequestDto;
import site.campingon.campingon.user.entity.Role;
import site.campingon.campingon.user.entity.User;
import site.campingon.campingon.user.mapper.UserMapper;
import site.campingon.campingon.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    // 회원 가입
    @Transactional
    public UserSignUpResponseDto registerUser(UserSignUpRequestDto userSignUpRequestDto) {

        Optional<User> existingUser = userRepository.findByEmailOrNicknameAndDeletedAtIsNull(
            userSignUpRequestDto.getEmail(),
            userSignUpRequestDto.getNickname()
        );

        if (existingUser.isPresent()) {
            User user = existingUser.get(); // NPE 방지
            if (user.getEmail().equals(userSignUpRequestDto.getEmail())) {
                throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
            }
            if (user.getNickname().equals(userSignUpRequestDto.getNickname())) {
                throw new IllegalArgumentException("이미 존재하는 닉네임입니다.");
            }
        }

        String encodedPassword = passwordEncoder.encode(userSignUpRequestDto.getPassword());

        User newUser = User.builder()
            .email(userSignUpRequestDto.getEmail())
            .nickname(userSignUpRequestDto.getNickname())
            .password(encodedPassword)
            .name(userSignUpRequestDto.getName())
            .role(Role.USER)
            .build();

        User user = userRepository.save(newUser);

        log.info("회원 가입 - 이메일: {}", user.getEmail());
        return userMapper.toSignUpResponseDto(user);
    }

    // 회원 정보 조회
    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo(Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return userMapper.toResponseDto(user);
    }


    // 회원 정보 수정(닉네임, 비밀번호)
    @Transactional
    public UserResponseDto updateUser(Long userId, UserUpdateRequestDto userUpdateRequestDto) {
        // 사용자 정보 조회
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(userUpdateRequestDto.getCurrentPassword(),
            user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 닉네임 변경
        if (userRepository.existsByNicknameAndDeletedAtIsNull(userUpdateRequestDto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
        user.updateNickname(userUpdateRequestDto.getNickname());

        // 새 비밀번호가 있는 경우에만 비밀번호 변경
        if (userUpdateRequestDto.getNewPassword() != null && !userUpdateRequestDto.getNewPassword().isBlank()) {

            // 현재 비밀번호와 새 비밀번호 비교
            if (userUpdateRequestDto.getNewPassword().equals(userUpdateRequestDto.getCurrentPassword())) {
                throw new IllegalArgumentException("새 비밀번호는 현재 비밀번호와 다르게 설정해야 합니다.");
            }

            if (!passwordEncoder.matches(userUpdateRequestDto.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
            }
            user.updatePassword(passwordEncoder.encode(userUpdateRequestDto.getNewPassword()));
        }

        // 변경된 사용자 정보 저장
        User updatedUser = userRepository.save(user);
        log.info("회원 정보 업데이트 - 이메일: {}", updatedUser.getEmail());
        return userMapper.toResponseDto(updatedUser);
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(UserDeactivateRequestDto userDeactiveRequestDto) {
        // 사용자 정보 조회
        User user = userRepository.findByIdAndDeletedAtIsNull(userDeactiveRequestDto.getId())
            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 사용자 탈퇴 처리 (소프트 삭제)
        user.deleteUser(userDeactiveRequestDto.getDeleteReason());
        log.info("회원 탈퇴 - 이메일: {} , 탈퇴 사유: {}", userDeactiveRequestDto.getId(), userDeactiveRequestDto.getDeleteReason());

    }

}
