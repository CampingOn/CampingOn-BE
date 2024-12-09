package site.campingon.campingon.review.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import site.campingon.campingon.common.jwt.CustomUserDetails;
import site.campingon.campingon.review.dto.ReviewCreateRequestDto;
import site.campingon.campingon.review.dto.ReviewResponseDto;
import site.campingon.campingon.review.dto.ReviewUpdateRequestDto;
import site.campingon.campingon.review.service.ReviewService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/camps")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 생성
    @PostMapping(value = "/{campId}/reviews/{reservationId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponseDto> createReview(
            @PathVariable("campId") Long campId,
            @PathVariable("reservationId") Long reservationId,
            @ModelAttribute ReviewCreateRequestDto requestDto
    ) {
        return ResponseEntity.ok(reviewService.createReview(campId, reservationId, requestDto));
    }


    // 리뷰 수정
    @PutMapping(value = "/{campId}/reviews/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponseDto> updateReview(
            @PathVariable("campId") Long campId,
            @PathVariable("reviewId") Long reviewId,
            @ModelAttribute ReviewUpdateRequestDto requestDto
    ) {
        return ResponseEntity.ok(reviewService.updateReview(campId, reviewId, requestDto));
    }

    // 캠핑장 id로 리뷰 목록 조회
    @GetMapping("/{campId}/reviews")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByCampId(
            @PathVariable("campId") Long campId
    ) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByCampId(campId);
        return ResponseEntity.ok(reviews);
    }

    // 리뷰 상세 조회
    @GetMapping("/{campId}/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReviewById(
            @PathVariable("reviewId") Long reviewId
    ) {
        ReviewResponseDto review = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(review);
    }

    // 리뷰 삭제
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable("reviewId") Long reviewId
    ) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }
}
