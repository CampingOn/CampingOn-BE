package site.campingon.campingon.camp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.campingon.campingon.common.converter.IndutyConverter;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "camp_site")
public class CampSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "camp_id", nullable = false)
    private Camp camp; // 캠핑장 (N:1 관계)

    @Column(name = "maximum_people", nullable = false)
    private Integer maximumPeople; // 최대 수용 인원

    @Column(nullable = false)
    private Integer price; // 가격

    @Column(length = 100, nullable = false)
    @Convert(converter = IndutyConverter.class) //converter 사용
    private Induty type; // 업종 구분

    @Column(name = "indoor_facility", length = 255)
    private String indoorFacility;

    // @Builder.Default
    @Column(name = "is_available", nullable = false)
    private boolean isAvailable;
}
