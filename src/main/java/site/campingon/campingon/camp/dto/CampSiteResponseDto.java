package site.campingon.campingon.camp.dto;

import lombok.*;
import site.campingon.campingon.camp.entity.Induty;
import site.campingon.campingon.reservation.entity.CheckTime;

import java.time.LocalTime;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampSiteResponseDto {
    private Long siteId;
    private Integer maximumPeople;
    private Integer price;
    private Induty siteType;
    private String indoorFacility;

    @Builder.Default
    private CheckTime checkinTime = CheckTime.CHECKIN;
    @Builder.Default
    private CheckTime checkoutTime = CheckTime.CHECKOUT;

    CampSimpleDto campSimpleDto;
}