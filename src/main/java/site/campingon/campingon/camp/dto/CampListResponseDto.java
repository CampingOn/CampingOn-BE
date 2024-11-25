package site.campingon.campingon.camp.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampListResponseDto {
  private Long id;
  private String name;
  private String lineIntro;
  private String thumbImage;

  private String address;  // 도로명 주소

  private List<String> keywords;   // 캠핑장 키워드

  private boolean isMarked;  // 찜 여부

}