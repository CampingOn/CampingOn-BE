package site.campingon.campingon.review.schedule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import site.campingon.campingon.common.s3bucket.service.S3BucketService;
import site.campingon.campingon.review.entity.Review;
import site.campingon.campingon.review.entity.ReviewImage;
import site.campingon.campingon.review.repository.ReviewImageRepository;
import site.campingon.campingon.review.repository.ReviewRepository;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class ReviewScheduler {
    private final ReviewRepository reviewRepository;
    private final ReviewImageRepository reviewImageRepository;
    private final S3BucketService s3BucketService;

    // 삭제된 지 3개월이 지난 리뷰의 이미지들을 정리하는 스케줄러
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void cleanupOldDeletedReviewImages() {
        LocalDateTime threeMonthsAgo = LocalDateTime.now().minusMonths(3);
        List<Review> oldDeletedReviews = reviewRepository.findByDeletedAtBefore(threeMonthsAgo);

        for (Review review : oldDeletedReviews) {
            List<ReviewImage> images = reviewImageRepository.findByReview(review);

            for (ReviewImage image : images) {

                try {
                    s3BucketService.remove(image.getImageUrl());
                    reviewImageRepository.delete(image);
                } catch (Exception e) {
                    log.error("Failed to cleanup review image: reviewId={}, imageUrl={}",
                        review.getId(), image.getImageUrl());
                }
            }
        }
    }


}
