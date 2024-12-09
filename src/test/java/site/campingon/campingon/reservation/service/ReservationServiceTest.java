package site.campingon.campingon.reservation.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import site.campingon.campingon.camp.entity.Camp;
import site.campingon.campingon.camp.entity.CampSite;
import site.campingon.campingon.camp.entity.Induty;
import site.campingon.campingon.common.exception.ErrorCode;
import site.campingon.campingon.common.exception.GlobalException;
import site.campingon.campingon.reservation.dto.*;
import site.campingon.campingon.reservation.entity.Reservation;
import site.campingon.campingon.reservation.entity.ReservationStatus;
import site.campingon.campingon.reservation.mapper.ReservationMapper;
import site.campingon.campingon.reservation.repository.ReservationRepository;
import site.campingon.campingon.reservation.utils.ReservationValidate;
import site.campingon.campingon.user.entity.User;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ReservationMapper reservationMapper;
    @Mock
    private ReservationValidate reservationValidate;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    private User mockUser;
    private Camp mockCamp;
    private CampSite mockCampSite;
    private Reservation mockReservation;
    private ReservationResponseDto mockReservationDto;
    private CampResponseDto mockCampDto;
    private CampAddrResponseDto mockCampAddrDto;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .email("bada@test.com")
                .nickname("bada")
                .build();

        mockCamp = Camp.builder()
                .id(1L)
                .campName("Test Camp")
                .thumbImage("Test Image")
                .build();

        mockCampSite = CampSite.builder()
                .id(1L)
                .camp(mockCamp)
                .siteType(Induty.NORMAL_SITE)
                .price(Induty.NORMAL_SITE.getPrice())
                .maximumPeople(Induty.NORMAL_SITE.getMaximumPeople())
                .build();

        mockReservation = Reservation.builder()
                .id(1L)
                .user(mockUser)
                .camp(mockCamp)
                .campSite(mockCampSite)
                .checkinDate(LocalDate.from(LocalDateTime.now()))
                .checkoutDate(LocalDate.from(LocalDateTime.now().plusDays(1)))
                .guestCnt(2)
                .status(ReservationStatus.RESERVED)
                .totalPrice(50000)
                .build();

        mockCampAddrDto = CampAddrResponseDto.builder()
                .city("TestCity")
                .state("TestState")
                .zipcode("TestZipcode")
                .streetAddr("Test Street")
                .detailedAddr("Test detailedAddr")
                .build();

        mockCampDto = CampResponseDto.builder()
                .id(1L)
                .name("Test Camp")
                .thumbImage("Test Image")
                .build();

        mockReservationDto = ReservationResponseDto.builder()
                .id(1L)
                .userId(mockUser.getId())
                .campSiteId(mockCampSite.getId())
                .guestCnt(mockReservation.getGuestCnt())
                .status(mockReservation.getStatus())
                .totalPrice(mockReservation.getTotalPrice())
                .campResponseDto(mockCampDto)
                .campAddrResponseDto(mockCampAddrDto)
                .build();
    }

    @Test
    @DisplayName("조회성공 - 유저의 예약 목록")
    void getReservationsSuccess() {

        // given
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        List<Reservation> reservations = List.of(mockReservation);
        Page<Reservation> reservationPage = new PageImpl<>(reservations);

        when(reservationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable))
            .thenReturn(reservationPage);
        when(reservationMapper.toResponse(mockReservation))
            .thenReturn(mockReservationDto);

        // when
        Page<ReservationResponseDto> result = reservationService.getReservations(userId, pageable);

        // then
        assertNotNull(result);
        assertTrue(result.hasContent());
        assertEquals(mockReservationDto, result.getContent().get(0));
        verify(reservationRepository).findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    @Test
    @DisplayName("생성성공 - 새 예약 생성")
    void createReservationSuccess() {

        // given
        ReservationCreateRequestDto requestDto = new ReservationCreateRequestDto(
            mockCamp.getId(),
            mockCampSite.getId(),
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            2,
            50000
        );

        when(reservationValidate.validateUserById(mockUser.getId()))
            .thenReturn(mockUser);
        when(reservationValidate.validateCampSiteById(requestDto.getCampSiteId()))
            .thenReturn(mockCampSite);

        // when
        reservationService.createReservation(mockUser.getId(), requestDto);

        // then
        verify(reservationRepository).save(any(Reservation.class));
        verify(reservationValidate).validateUserById(mockUser.getId());
        verify(reservationValidate).validateCampSiteById(requestDto.getCampSiteId());
    }

    @Test
    @DisplayName("변경성공 - 예약 취소")
    void cancelReservationSuccess() {

        // given
        ReservationCancelRequestDto requestDto = new ReservationCancelRequestDto(
            mockUser.getId(),
            mockCamp.getId(),
            mockReservation.getId(),
            ReservationStatus.CANCELED,
            "개인 사정"
        );

        when(reservationValidate.validateReservationById(requestDto.getId()))
            .thenReturn(mockReservation);

        // when
        reservationService.cancelReservation(mockUser.getId(), requestDto.getId(), requestDto);

        // then
        verify(reservationRepository).save(any(Reservation.class));
        verify(reservationValidate).validateReservationById(requestDto.getId());
    }

    @Test
    @DisplayName("예약실패 - 없는 사용자 ID로 예약")
    void createReservationUserNotFound() {
        // given
        ReservationCreateRequestDto requestDto = new ReservationCreateRequestDto(
            mockCamp.getId(),
            mockCampSite.getId(),
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            2,
            50000
        );

        Long wrongId = 999L; // 존재하지 않는 유저 ID
        when(reservationValidate.validateUserById(wrongId))
            .thenThrow(new GlobalException(ErrorCode.USER_NOT_FOUND_BY_ID));

        // when & then
        assertThrows(GlobalException.class, () -> 
            reservationService.createReservation(wrongId, requestDto));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("예약실패 - 존재하지 않는 캠핑장 예약")
    void createReservationCampSiteNotFound() {
        // given
        ReservationCreateRequestDto requestDto = new ReservationCreateRequestDto(
            mockCamp.getId(),
            mockCampSite.getId(),
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            2,
            50000
        );

        when(reservationValidate.validateUserById(mockUser.getId()))
            .thenReturn(mockUser);
        when(reservationValidate.validateCampSiteById(requestDto.getCampId()))
            .thenThrow(new GlobalException(ErrorCode.CAMP_NOT_FOUND_BY_ID));

        // when & then
        assertThrows(GlobalException.class, () -> 
            reservationService.createReservation(mockUser.getId(), requestDto));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("조회실패 - 없는 예약 ID로 조회")
    void getReservationNotFound() {
        // given
        Long invalidReservationId = 999L;
        when(reservationValidate.validateReservationById(invalidReservationId))
            .thenThrow(new GlobalException(ErrorCode.RESERVATION_NOT_FOUND_BY_ID));

        // when & then
        assertThrows(GlobalException.class, () -> 
            reservationService.getReservation(mockUser.getId(), invalidReservationId));
    }

    @Test
    @DisplayName("취소실패 - 없는 예약 ID로 취소")
    void cancelReservationNotFound() {
        // given
        ReservationCancelRequestDto requestDto = new ReservationCancelRequestDto(
            999L,
            mockUser.getId(),
            mockCamp.getId(),
            ReservationStatus.CANCELED,
            "개인 사정"
        );

        when(reservationValidate.validateReservationById(requestDto.getId()))
            .thenThrow(new GlobalException(ErrorCode.RESERVATION_NOT_FOUND_BY_ID));

        // when & then
        assertThrows(GlobalException.class, () -> 
            reservationService.cancelReservation(mockUser.getId(), requestDto.getId(), requestDto));
        verify(reservationRepository, never()).save(any());
    }

    @Test
    @DisplayName("취소실패 - 이미 취소된 예약에 대한 취소")
    void cancelReservationAlreadyCanceled() {

        // given
        Reservation canceledReservation = mockReservation.toBuilder()
            .status(ReservationStatus.CANCELED)
            .build();

        ReservationCancelRequestDto requestDto = new ReservationCancelRequestDto(
            canceledReservation.getId(),
            mockUser.getId(),
            mockCamp.getId(),
            ReservationStatus.CANCELED,
            "개인 사정"
        );

        when(reservationValidate.validateReservationById(requestDto.getId()))
            .thenReturn(canceledReservation);
        
        // validateStatus에서 예외 발생하도록 설정
        doThrow(new GlobalException(ErrorCode.RESERVATION_ALREADY_CANCELED))
            .when(reservationValidate)
            .validateStatus(requestDto.getStatus());

        // when & then
        GlobalException exception = assertThrows(GlobalException.class, () ->
            reservationService.cancelReservation(mockUser.getId(), requestDto.getId(), requestDto));
        
        assertEquals(ErrorCode.RESERVATION_ALREADY_CANCELED, exception.getErrorCode());
        verify(reservationValidate).validateReservationById(requestDto.getId());
        verify(reservationValidate).validateStatus(requestDto.getStatus());
        verify(reservationRepository, never()).save(any());
    }
}