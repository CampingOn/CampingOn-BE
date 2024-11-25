package site.campingon.campingon.camp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "camp_addr")
public class CampAddr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @OneToOne
    @JoinColumn(name = "camp_id", nullable = false)
    private Camp camp;

    @Column(length = 50)
    private String city; // 도/광역시

    @Column(length = 50)
    private String state; // 시/군/구

    @Column(length = 20)
    private String zipcode;  // 우편번호

    @Column(nullable = false)
    private Double longitude;  // mayX - 경도

    @Column(nullable = false)
    private Double latitude;  // maxY - 위도

    @Column(name = "street_addr", length = 50)
    private String streetAddr;   // 기본 도로명 주소

    @Column(name = "detailed_addr", length = 50)
    private String detailedAddr;   // 상세 주소 (없는 경우 많음)

}