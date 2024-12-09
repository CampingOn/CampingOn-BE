package site.campingon.campingon.reservation.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.campingon.campingon.reservation.dto.*;

public interface ReservationService {

    // 유저의 모든 예약리스트를 조회
    Page<ReservationResponseDto> getReservations(Long userId, Pageable pageable);

    // 예약완료 직후 확인을 위해 예약 정보 조회
    ReservationResponseDto getReservation(Long userId, Long reservationId);

    // 캠프사이트 선택 후 예약 요청
    void createReservation(Long userId, ReservationCreateRequestDto requestDto);

    // 예약완료 이후 예약취소 요청
    void cancelReservation(Long userId, Long reservationId, ReservationCancelRequestDto requestDto);

    // 가장 가까운 예약 정보 조회
    ReservationResponseDto getUpcomingReservation(Long userId);

}
