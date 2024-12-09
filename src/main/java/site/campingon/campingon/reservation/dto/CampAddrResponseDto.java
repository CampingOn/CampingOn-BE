package site.campingon.campingon.reservation.dto;

import lombok.*;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampAddrResponseDto {

    private String city;

    private String state;

    private String zipcode;

    private String streetAddr;

    private String detailedAddr;

}
