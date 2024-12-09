package site.campingon.campingon.user.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.campingon.campingon.common.exception.ErrorCode;
import site.campingon.campingon.common.exception.GlobalException;
import site.campingon.campingon.common.jwt.RefreshTokenService;
import site.campingon.campingon.user.dto.KeywordResponseDto;
import site.campingon.campingon.user.dto.UserResponseDto;
import site.campingon.campingon.user.dto.UserSignUpRequestDto;
import site.campingon.campingon.user.dto.UserSignUpResponseDto;
import site.campingon.campingon.user.dto.UserUpdateRequestDto;
import site.campingon.campingon.user.entity.Role;
import site.campingon.campingon.user.entity.User;
import site.campingon.campingon.user.entity.UserKeyword;
import site.campingon.campingon.user.mapper.UserMapper;
import site.campingon.campingon.user.repository.UserKeywordRepository;
import site.campingon.campingon.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserKeywordRepository userKeywordRepository;
    private final RefreshTokenService refreshTokenService;


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
                throw new GlobalException(ErrorCode.DUPLICATED_EMAIL);
            }
            if (user.getNickname().equals(userSignUpRequestDto.getNickname())) {
                throw new GlobalException(ErrorCode.DUPLICATED_NICKNAME);
            }
        }

        String encodedPassword = passwordEncoder.encode(userSignUpRequestDto.getPassword());

        User newUser = User.builder()
            .email(userSignUpRequestDto.getEmail())
            .nickname(userSignUpRequestDto.getNickname())
            .password(encodedPassword)
            .name(userSignUpRequestDto.getName())
            .role(Role.ROLE_USER)
            .build();

        User user = userRepository.save(newUser);

        log.debug("회원 가입 - 이메일: {}", user.getEmail());
        return userMapper.toSignUpResponseDto(user);
    }

    // 회원 정보 조회
    @Transactional(readOnly = true)
    public UserResponseDto getMyInfo(Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND_BY_ID));

        return userMapper.toResponseDto(user);
    }

    // 이메일로 회원 검색
    @Transactional(readOnly = true)
    public User findUserByEmail(String email) {
        return userRepository.findByEmailAndDeletedAtIsNull(email)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND_BY_EMAIL));

    }


    // 회원 정보 수정(닉네임, 비밀번호)
    @Transactional
    public UserResponseDto updateUser(Long userId, UserUpdateRequestDto userUpdateRequestDto) {
        // 사용자 정보 조회
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND_BY_ID));

        // 현재 비밀번호 확인
        if (!passwordEncoder.matches(userUpdateRequestDto.getCurrentPassword(),
            user.getPassword())) {
            throw new GlobalException(ErrorCode.PASSWORD_MISMATCH);
        }

        // 닉네임 변경
        if (userRepository.existsByNickname(userUpdateRequestDto.getNickname())) {
            throw new GlobalException(ErrorCode.DUPLICATED_NICKNAME);
        }
        user.updateNickname(userUpdateRequestDto.getNickname());

        // 새 비밀번호가 있는 경우에만 비밀번호 변경
        if (userUpdateRequestDto.getNewPassword() != null && !userUpdateRequestDto.getNewPassword().isBlank()) {

            // 현재 비밀번호와 새 비밀번호 비교
            if (userUpdateRequestDto.getNewPassword().equals(userUpdateRequestDto.getCurrentPassword())) {
                throw new GlobalException(ErrorCode.PASSWORD_SAME_AS_OLD);
            }

            if (!passwordEncoder.matches(userUpdateRequestDto.getCurrentPassword(), user.getPassword())) {
                throw new GlobalException(ErrorCode.PASSWORD_MISMATCH);
            }
            user.updatePassword(passwordEncoder.encode(userUpdateRequestDto.getNewPassword()));
        }

        // 변경된 사용자 정보 저장
        User updatedUser = userRepository.save(user);
        log.debug("회원 정보 업데이트 - 이메일: {}", updatedUser.getEmail());
        return userMapper.toResponseDto(updatedUser);
    }

    // 회원 탈퇴
    @Transactional
    public void deleteUser(Long userId, String deleteReason) {
        // 사용자 정보 조회
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND_BY_ID));

        refreshTokenService.deleteRefreshTokenByEmail(user.getEmail());

        // 사용자 탈퇴 처리 (소프트 삭제)
        user.deleteUser(deleteReason);
        log.debug("회원 탈퇴 - 이메일: {} , 탈퇴 사유: {}", userId, deleteReason);

    }

    // 중복 확인(회원 가입시 이메일, 닉네임 부분)
    public boolean checkDuplicate(String type, String value) {
        return switch (type.toLowerCase()) {
            case "email" -> userRepository.existsByEmailAndDeletedAtIsNull(value);
            case "nickname" -> userRepository.existsByNickname(value);
            default -> throw new IllegalArgumentException("잘못된 타입 입력값입니다.");
        };
    }

    // 나의 키워드 조회
    @Transactional(readOnly = true)
    public KeywordResponseDto getKeywordsByUserId(Long userId) {
        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND_BY_ID));

        List<String> userKeywords = user.getKeywords().stream()
            .map(UserKeyword::getKeyword)
            .toList();

        log.debug("회원 키워드 조회 - 유저 ID: {}, 키워드: {}", userId, userKeywords);
        return new KeywordResponseDto(userKeywords);
    }

    @Transactional
    public void replaceKeywords(Long userId, List<String> keywords) {
        // Validate keyword size (optional, assuming max 5)
        if (keywords.size() > 5) {
            throw new GlobalException(
                ErrorCode.KEYWORD_LIMIT_EXCEEDED); // Custom error for exceeding limit
        }

        User user = userRepository.findByIdAndDeletedAtIsNull(userId)
            .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND_BY_ID));

        // 기존 키워드 제거
        user.getKeywords().clear();

        // 새 키워드 추가
        for (String keyword : keywords) {
            UserKeyword newKeyword = UserKeyword.builder()
                .user(user)
                .keyword(keyword)
                .build();
            userKeywordRepository.save(newKeyword);
        }

        log.info("회원 키워드 갱신 - 유저 ID: {}, 새 키워드 목록: {}", userId, keywords);
    }


}
