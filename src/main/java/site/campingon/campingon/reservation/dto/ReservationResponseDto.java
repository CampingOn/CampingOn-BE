package site.campingon.campingon.reservation.dto;

import lombok.*;
import site.campingon.campingon.reservation.entity.CheckTime;
import site.campingon.campingon.reservation.entity.ReservationStatus;

import java.time.LocalDate;

@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDto {

    private Long id;

    private LocalDate checkinDate;

    private LocalDate checkoutDate;

    private CheckTime checkinTime;

    private CheckTime checkoutTime;

    private int guestCnt;

    private ReservationStatus status;

    private int totalPrice;

    private CampResponseDto campResponseDto;

    private CampAddrResponseDto campAddrResponseDto;

    private CampSiteResponseDto campSiteResponseDto;

    private ReviewResponseDto reviewResponseDto;

}
