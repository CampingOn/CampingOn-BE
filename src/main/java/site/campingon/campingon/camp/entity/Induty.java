package site.campingon.campingon.camp.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Induty {
    NORMAL_SITE("일반야영장", 25000, 6),
    CAR_SITE("자동차야영장", 35000, 6),
    GLAMP_SITE("글램핑", 70000, 4),
    CARAV_SITE("카라반", 80000, 4),
    PERSONAL_CARAV_SITE("카라반(개인)", 35000, 6);

    @JsonValue
    private final String type;
    private final Integer price;
    private final Integer maximumPeople;
}
