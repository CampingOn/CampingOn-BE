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
    private String zipcode;

    @Column(name = "street_addr", length = 50)
    private String streetAddr;

    @Column(name = "detailed_addr", length = 50)
    private String detailedAddr;

    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();

        // city 추가
        if (city != null && !city.isBlank()) {
            fullAddress.append(city).append(" ");
        }

        // state 추가
        if (state != null && !state.isBlank()) {
            fullAddress.append(state).append(" ");
        }

        // streetAddr 추가
        if (streetAddr != null && !streetAddr.isBlank()) {
            fullAddress.append(streetAddr).append(" ");
        }

        // detailedAddr 추가
        if (detailedAddr != null && !detailedAddr.isBlank()) {
            fullAddress.append(detailedAddr).append(" ");
        }

        // 공백 제거 후 반환
        return fullAddress.toString().trim();
    }
}
