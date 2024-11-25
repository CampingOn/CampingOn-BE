package site.campingon.campingon.camp.dto;


import lombok.*;

import java.time.LocalTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampSiteListResponseDto {
  private Long siteId;
  private String roomName;
  private Integer maxPeople;
  private Integer price;
  private String imageUrl;
  private String indoor_facility;
  private String type;

  @Builder.Default  // 기본값 자동 설정
  private LocalTime checkInTime = LocalTime.of(15, 0);  // 15:00

  @Builder.Default  // 기본값 자동 설정
  private LocalTime checkOutTime = LocalTime.of(11, 0);  // 11:00

}