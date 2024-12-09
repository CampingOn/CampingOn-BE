package site.campingon.campingon.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import site.campingon.campingon.common.jwt.CustomUserDetails;
import site.campingon.campingon.user.dto.UserResponseDto;
import site.campingon.campingon.user.dto.UserSignUpRequestDto;
import site.campingon.campingon.user.dto.UserSignUpResponseDto;
import site.campingon.campingon.user.dto.UserUpdateRequestDto;
import site.campingon.campingon.user.service.UserService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    // 회원 가입 api
    @PostMapping("/signup")
    public ResponseEntity<UserSignUpResponseDto> registerUser(@RequestBody @Valid UserSignUpRequestDto userSignUpRequestDto) {
        UserSignUpResponseDto userSignUpResponseDto = userService.registerUser(userSignUpRequestDto);
        log.debug("회원가입 성공: email={}", userSignUpRequestDto.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(userSignUpResponseDto);
    }

    // 회원 정보 조회 api
    @GetMapping("/users/me")
    public ResponseEntity<UserResponseDto> getMyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        UserResponseDto userInfo = userService.getMyInfo(userId);

        return ResponseEntity.ok(userInfo);
    }

    // 회원 정보 수정 api
    @PutMapping("/users/me")
    public ResponseEntity<UserResponseDto> updateMyInfo(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody @Valid UserUpdateRequestDto userUpdateRequestDto
    ) {

        Long userId = userDetails.getId();

        return ResponseEntity.ok(userService.updateUser(userId, userUpdateRequestDto));
    }

    // 중복 확인 api
    @GetMapping("/users/check-duplicate")
    public boolean checkDuplicate(
        @RequestParam(value = "type") String type,
        @RequestParam(value = "value") String value) {

        return userService.checkDuplicate(type, value);
    }

    // 회원 탈퇴
    @DeleteMapping("/users/me")
    public ResponseEntity<Void> deleteUser(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody String deleteReason
    ) {
        Long userId = userDetails.getId();
        userService.deleteUser(userId, deleteReason);

        return ResponseEntity.noContent().build();
    }



}
