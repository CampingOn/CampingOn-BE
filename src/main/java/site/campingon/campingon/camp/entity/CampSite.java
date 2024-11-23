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
@Table(name = "camp_site")
public class CampSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camp_id", nullable = false)
    private Camp camp; // 캠핑장 (N:1 관계)

    @Column(name = "room_name", length = 20, nullable = false)
    private String roomName; // 캠핑지 이름

    @Column(name = "maximum_people", nullable = false)
    private Integer maximumPeople; // 최대 수용 인원

    @Column(nullable = false)
    private Integer price; // 가격

    @Column(name = "image_url", length = 255)
    private String imageUrl; // 캠핑지 이미지

    @Column(length = 100, nullable = false)
    private String induty; // 업종 구분

    @Column(name = "indoor_facility", length = 255)
    private String indoorFacility;
}
