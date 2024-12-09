package site.campingon.campingon.camp.dto;

import lombok.*;

@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampSimpleDto {

    private Long campId;

    private String campName;

    private String city;

    private String state;

    private String streetAddr;

}
