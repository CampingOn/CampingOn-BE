package site.campingon.campingon.camp.dto.admin;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampCreateRequestDto {
    private String name;           // 캠핑장 이름
    private String tel;            // 전화번호
    private String lineIntro;      // 요약 소개
    private String homepage;       // 홈페이지 URL
    private String outdoorFacility; // 부대시설
    private String address;        // 도로명 주소
    private List<String> images;   // 이미지 URL 리스트
}