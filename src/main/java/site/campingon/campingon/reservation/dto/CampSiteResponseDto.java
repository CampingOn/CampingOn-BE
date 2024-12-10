package site.campingon.campingon.reservation.dto;

import lombok.*;
import site.campingon.campingon.camp.entity.Induty;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampSiteResponseDto {

    private Long siteId;

    private String indoorFacility;

    private Induty siteType;

}
