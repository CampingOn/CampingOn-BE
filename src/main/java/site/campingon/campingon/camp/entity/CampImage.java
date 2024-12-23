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
@Table(name = "camp_image")
public class CampImage {

    @Id
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "camp_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private Camp camp;

    @Column(name = "image_url", length = 255, nullable = false)
    private String imageUrl;
}

