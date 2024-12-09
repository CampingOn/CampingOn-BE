package site.campingon.campingon.reservation.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import site.campingon.campingon.reservation.dto.ReservationCancelRequestDto;
import site.campingon.campingon.reservation.dto.ReservationCreateRequestDto;
import site.campingon.campingon.reservation.dto.ReservationResponseDto;
import site.campingon.campingon.reservation.entity.Reservation;

@Mapper(componentModel = "spring")
public interface ReservationMapper {
    @Mapping(source = "checkin", target = "checkinDate")
    @Mapping(source = "checkout", target = "checkoutDate")
    Reservation toEntity(ReservationCreateRequestDto reservationRequest);

    Reservation toEntity(ReservationCancelRequestDto reservationRequest);

    @Mapping(source = "campSite", target = "campSiteResponseDto")
    @Mapping(source = "camp.campAddr", target = "campAddrResponseDto")
    @Mapping(source = "camp", target = "campResponseDto")
    @Mapping(source = "review", target = "reviewResponseDto")
    ReservationResponseDto toResponse(Reservation reservation);

}