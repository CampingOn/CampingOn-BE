package site.campingon.campingon.camp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampInfoDto {
    private Long campInfoId;
    private Integer recommendCnt;
    private Integer bookmarkCnt;
}