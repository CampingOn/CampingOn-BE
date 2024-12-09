package site.campingon.campingon.user.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.campingon.campingon.common.jwt.CustomUserDetails;
import site.campingon.campingon.user.dto.KeywordRequestDto;
import site.campingon.campingon.user.dto.KeywordResponseDto;
import site.campingon.campingon.user.entity.KeywordEnum;
import site.campingon.campingon.user.service.UserService;

@RestController
@RequestMapping("/api/keywords")
@RequiredArgsConstructor
public class UserKeywordController {

    private final UserService userService;

    // 선택할 키워드 목록 조회
    @GetMapping
    public ResponseEntity<KeywordResponseDto> getAvailableKeywords() {
        // Enum에서 displayName 값 추출
        List<String> keywords = Arrays.stream(KeywordEnum.values())
            .map(KeywordEnum::getDisplayName)
            .collect(Collectors.toList());
        return ResponseEntity.ok(new KeywordResponseDto(keywords));
    }


    // 개인 키워드 목록 조회
    @GetMapping("/me")
    public ResponseEntity<KeywordResponseDto> getMyKeyword(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        return ResponseEntity.ok(userService.getKeywordsByUserId(userId));
    }

    // 선택한 키워드 목록 저장
    @PostMapping("/me")
    public ResponseEntity<Void> replaceKeywords(
        @AuthenticationPrincipal CustomUserDetails userDetails,
        @RequestBody KeywordRequestDto keywordRequest
    ) {
        Long userId = userDetails.getId();
        List<String> keywords = keywordRequest.getKeywords();
        userService.replaceKeywords(userId, keywords);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
