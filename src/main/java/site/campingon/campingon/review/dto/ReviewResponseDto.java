package site.campingon.campingon.review.dto;

import lombok.*;
import site.campingon.campingon.review.entity.ReviewImage;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private Long reviewId;  // 리뷰 ID
    private Long campId; // 캠핑장 ID
    private Long reservationId; // 예약 ID
    private Long userId; // 작성자 ID
    private String title;  // 리뷰 제목
    private String content; // 리뷰 내용
    private boolean isRecommend; // 추천 여부
    private List<ReviewImage> images;
}