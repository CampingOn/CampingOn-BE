package site.campingon.campingon.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import site.campingon.campingon.review.dto.ReviewCreateRequestDto;
import site.campingon.campingon.review.dto.ReviewResponseDto;
import site.campingon.campingon.review.dto.ReviewUpdateRequestDto;
import site.campingon.campingon.review.repository.ReviewRepository;
import site.campingon.campingon.review.service.ReviewService;
import site.campingon.campingon.user.entity.User;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/camps")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 생성
    @PostMapping(value = "/{campId}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponseDto> createReview(
            @PathVariable("campId") Long campId,
            @PathVariable("reservationId") Long reservationId,
            @ModelAttribute ReviewCreateRequestDto requestDto
    ) throws IOException {
        return ResponseEntity.ok(reviewService.createReview(campId, reservationId, requestDto));
    }


    // 리뷰 수정
    @PutMapping(value = "/{campId}/reviews/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable("campId") Long campId,
            @PathVariable("reviewId") Long reviewId,
            @ModelAttribute ReviewUpdateRequestDto requestDto
    ) throws IOException {
        return ResponseEntity.ok(reviewService.updateReview(campId, reviewId, requestDto));
    }


    // 캠핑장으로 리뷰 조회
    @GetMapping("/camp/{campId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByCampId(
            @PathVariable("campId") Long campId
    ) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByCampId(campId);
        return ResponseEntity.ok(reviews);
    }

    // 필요 없는거 같음
    // 캠핑지로 리뷰 조회
    @GetMapping("/campsite/{campSiteId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByCampSiteId(
            @PathVariable("campSiteId") Long campSiteId
    ) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByCampSiteId(campSiteId);
        return ResponseEntity.ok(reviews);
    }

    // 리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable("reviewId") Long reviewId
    ) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    // 리뷰 추천
    @PatchMapping("/{reviewId}/recommend")
    public ResponseEntity<Boolean> toggleRecommend(
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        User user = (User) userDetails;
        boolean isRecommended = reviewService.toggleRecommend(reviewId, user.getId());
        return ResponseEntity.ok(isRecommended);
    }
}
