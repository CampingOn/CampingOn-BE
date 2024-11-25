package site.campingon.campingon.camp.dto.admin;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampSiteUpdateRequestDto {

    @NotBlank(message = "캠핑지 이름은 필수입니다.")
    @Size(max = 20, message = "캠핑지 이름은 최대 20자까지 가능합니다.")
    private String roomName;

    @NotNull(message = "최대 수용 인원은 필수입니다.")
    @Min(value = 1, message = "최대 수용 인원은 최소 1명 이상이어야 합니다.")
    @Max(value = 10, message = "최대 수용 인원은 10명을 초과할 수 없습니다.")
    private Integer maximumPeople;

    @NotNull(message = "가격은 필수입니다.")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private Integer price;

    @NotBlank(message = "이미지 URL은 필수입니다.")
//    @Pattern(regexp = "^(http|https)://.*$", message = "유효한 URL 형식이어야 합니다.")
    private String imageUrl;

    @Size(max = 100, message = "업종 구분 설명은 최대 100자까지 가능합니다.") // Optional, 최대 길이 제한
    private String type;

    @NotNull(message = "사용 가능 여부는 필수입니다.")
    private boolean isAvailable;

    @NotNull(message = "체크인 시간은 필수입니다.")
    @FutureOrPresent(message = "체크인 시간은 현재 시각 또는 미래여야 합니다.")
    private LocalTime checkInTime;

    @NotNull(message = "체크아웃 시간은 필수입니다.")
    private LocalTime checkOutTime;
}