package site.campingon.campingon.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import site.campingon.campingon.reservation.entity.ReservationStatus;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCancelRequestDto {

    private Long id;

    private Long campId;

    private Long campSiteId;

    private ReservationStatus status;

    private String cancelReason;

}
