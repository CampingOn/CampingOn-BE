package site.campingon.campingon.camp.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampSiteResponseDto {
    private Long siteId;              // 캠핑지 ID
    private String roomName;          // 캠핑지 이름
    private Integer maxPeople;        // 최대 수용 인원
    private Integer price;            // 가격
    private String imageUrl;          // 대표 이미지 URL
    private String indoorFacility;    // 실내 시설 설명
    private String type;              // 업종 구분

    private LocalTime checkInTime;    // 체크인 시간
    private LocalTime checkOutTime;   // 체크아웃 시간
}