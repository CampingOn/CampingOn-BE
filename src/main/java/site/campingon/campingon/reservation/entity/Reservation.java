package site.campingon.campingon.reservation.entity;

import jakarta.persistence.*;
import lombok.*;
import site.campingon.campingon.camp.entity.Camp;
import site.campingon.campingon.camp.entity.CampSite;
import site.campingon.campingon.common.entity.BaseEntity;
import site.campingon.campingon.review.entity.Review;
import site.campingon.campingon.user.entity.User;

import java.time.LocalDate;

@Entity
@Getter
@ToString
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, columnDefinition = "INT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Camp camp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private CampSite campSite;

    @OneToOne(mappedBy = "reservation", fetch = FetchType.EAGER)
    private Review review;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate checkinDate;

    @Column(nullable = false, columnDefinition = "DATE")
    private LocalDate checkoutDate;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "TIME(0)")
    private CheckTime checkinTime = CheckTime.CHECKIN;

    @Builder.Default
    @Column(nullable = false, columnDefinition = "TIME(0)")
    private CheckTime checkoutTime = CheckTime.CHECKOUT;

    @Column(nullable = false)
    private int guestCnt;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'RESERVED'")
    private ReservationStatus status = ReservationStatus.RESERVED;

    private String cancelReason;

    private int totalPrice;

    // 예약 취소
    public void cancel(String reason) {
        this.status = ReservationStatus.CANCELED;
        this.cancelReason = reason;
    }

}
