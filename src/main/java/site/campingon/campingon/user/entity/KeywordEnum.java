package site.campingon.campingon.user.entity;

import lombok.Getter;

@Getter
public enum KeywordEnum {
    HOT_WATER_PROVIDED("온수 잘 나오는"),
    HEALING("힐링"),
    FRIENDLY("친절한"),
    STAR_VIEWING("별 보기 좋은"),
    COUPLE("커플"),
    LOTS_OF_SHADE("그늘이 많은"),
    FAMILY("가족"),
    SWIMMING("물놀이 하기 좋은"),
    OCEAN_VIEW("바다가 보이는"),
    GOOD_FOR_KIDS("아이들 놀기 좋은"),
    RELAXING("여유있는"),
    CLEAN("깨끗한"),
    EASY_CAR_PARKING("차대기 편한"),
    FUN("재미있는"),
    WIDE_SITE_SPACING("사이트 간격이 넓은");

    private final String displayName;

    KeywordEnum(String displayName) {
        this.displayName = displayName;
    }

}