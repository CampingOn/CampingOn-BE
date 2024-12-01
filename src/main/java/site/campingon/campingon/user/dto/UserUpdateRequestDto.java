package site.campingon.campingon.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserUpdateRequestDto {

    @NotBlank(message = "닉네임은 공백일 수 없습니다.")
    @Size(max = 8, message = "닉네임은 8자 이내여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣]+$", message = "닉네임은 알파벳, 숫자, 한글만 포함할 수 있습니다.")
    private String nickname;

    @NotBlank(message = "현재 비밀번호는 공백일 수 없습니다.")
    private String currentPassword; // 비밀번호 변경을 위해 기존 비밀번호 확인


    @Size(min = 8, max = 20, message = "새 비밀번호는 8자 이상, 20자 이하여야 합니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
        message = "비밀번호는 최소 8자 이상이어야 하며, 알파벳, 숫자, 특수문자를 포함해야 합니다.")
    private String newPassword; // 새 비밀번호
}