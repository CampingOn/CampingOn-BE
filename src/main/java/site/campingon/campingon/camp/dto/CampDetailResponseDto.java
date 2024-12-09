package site.campingon.campingon.camp.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampDetailResponseDto {
  private Long campId;
  private String name;
  private String tel;
  private String intro;
  private String lineIntro;
  private String homepage;
  private String outdoorFacility;
  private List<String> indutys;

  private CampAddrDto campAddr;

  private List<String> images;   // 캠핑장 이미지(썸네일 포함) <- Camp_image 엔티티

  private CampInfoDto campInfo;
}