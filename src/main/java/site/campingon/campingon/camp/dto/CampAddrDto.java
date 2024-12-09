package site.campingon.campingon.camp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampAddrDto {
    private Long campAddrId;
    private String city;
    private String state;
    private String zipcode;
    private String streetAddr;
    private String detailedAddr;
    private double latitude;
    private double longitude;
}
