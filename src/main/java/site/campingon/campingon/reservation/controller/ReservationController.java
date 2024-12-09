package site.campingon.campingon.reservation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.campingon.campingon.common.jwt.CustomUserDetails;
import site.campingon.campingon.reservation.dto.*;
import site.campingon.campingon.reservation.service.ReservationService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reservations")
public class ReservationController {

    @Value("${app.pagination.reservation.size}")
    private int size;


    private final ReservationService reservationService;

    // 유저의 예약 목록 조회
    @GetMapping
    public ResponseEntity<Page<ReservationResponseDto>> getReservations(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                        @RequestParam(value = "page", defaultValue = "0") int page) {

        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(reservationService.getReservations(userDetails.getId(), pageable));
    }

    // 단일 예약 정보 조회
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationResponseDto> getReservation(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                 @PathVariable("reservationId") Long reservationId) {

        return ResponseEntity.ok(reservationService.getReservation(userDetails.getId(), reservationId));
    }

    // 가장 가까운 예약 정보 조회
    @GetMapping("/upcoming")
    public ResponseEntity<ReservationResponseDto> getReservation(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(reservationService.getUpcomingReservation(userDetails.getId()));
    }

    // 새로운 예약 생성
    @PostMapping
    public ResponseEntity<Void> createReservation(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @RequestBody ReservationCreateRequestDto requestDto) {

        reservationService.createReservation(userDetails.getId(), requestDto);

        return ResponseEntity.ok().build();
    }

    // 예약 취소
    @PatchMapping("/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @PathVariable("reservationId") Long reservationId,
                                                  @RequestBody ReservationCancelRequestDto requestDto) {

        reservationService.cancelReservation(userDetails.getId(), reservationId, requestDto);

        return ResponseEntity.ok().build();
    }

}
