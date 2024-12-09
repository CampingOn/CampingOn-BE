package site.campingon.campingon.review.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "review_image")
public class ReviewImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(columnDefinition = "INT UNSIGNED")
    private Long id; // 리뷰 이미지 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false, columnDefinition = "INT UNSIGNED")
    private Review review; // 리뷰 ID

    @Column(name = "image_url", length = 255)
    private String imageUrl; // 이미지 URL

    public void updateImageUrl(String newUrl) {
        this.imageUrl = newUrl;
    }
}