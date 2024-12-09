package site.campingon.campingon.common.public_data.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GoCampingDataDto {
    private Response response;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
    public static class Response {
        private Body body;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
    public static class Body {
        private Items items;
        private long numOfRows;
        private long totalCount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
    public static class Items {
        private List<Item> item;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
    public static class Item {
        private long contentId; //콘텐츠 ID
        private String facltNm; //야영장명
        private String lineIntro;   //한줄소개
        private String intro;   //소개
        private String doNm;    //도
        private String sigunguNm;   //시군구
        private String zipcode; //우편번호
        private String addr1;   //주소
        private String addr2;   //상세주소
        private String tel; //전화
        private String homepage;    //홈페이지
        private Integer gnrlSiteCo;  //주요시설 일반야영장
        private Integer autoSiteCo;  //주요시설 자동차야영장
        private Integer glampSiteCo; //주요시설 글램핑
        private Integer caravSiteCo; //주요시설 카라반
        private Integer indvdlCaravSiteCo;   //주요시설 개인 카라반
        private String glampInnerFclty; //글램핑 - 내부시설
        private String caravInnerFclty; //카라반 - 내부시설
        private String sbrsCl;  //부대시설(외부시설)
        private String animalCmgCl; //애완동물출입("불가능, 가능")
        private String firstImageUrl;   //대표이미지
        private Double mapX;
        private Double mapY;
        private String createdtime; //등록일
        private String modifiedtime;    //수정일
    }
}
