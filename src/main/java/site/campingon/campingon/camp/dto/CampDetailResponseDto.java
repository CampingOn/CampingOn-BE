package site.campingon.campingon.camp.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampDetailResponseDto {
  private Long id;
  private String name;
  private String tel;
  private String lineIntro;
  private String homepage;   // 홈페이지 사용 고려
  private String outdoorFacility;   // 부대시설
  private String induty;  // 업종

  private Double longitude;  // mayX - 경도
  private Double latitude;  // maxY - 위도
  private String address;   // 도로명 주소

  private List<String> images;   // 캠핑장 이미지(썸네일 포함) <- Camp_image 엔티티

  private int recommendCnt;  // 추천 수 <- Camp_Info 엔티티
  private int bookmarkCnt;  // 찜 수 <- Camp_Info 엔티티
}
