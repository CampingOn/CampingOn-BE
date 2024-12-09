package site.campingon.campingon.review.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import site.campingon.campingon.camp.entity.Camp;
import site.campingon.campingon.reservation.entity.Reservation;
import site.campingon.campingon.review.dto.ReviewCreateRequestDto;
import site.campingon.campingon.review.dto.ReviewResponseDto;
import site.campingon.campingon.review.dto.ReviewUpdateRequestDto;
import site.campingon.campingon.review.entity.Review;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReviewMapper {

    // 리뷰 생성 매퍼
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "reservation.user")
    @Mapping(target = "campSite", source = "reservation.campSite")
    @Mapping(target = "reservation", source = "reservation")
    @Mapping(target = "camp", source = "camp")
    // @Mapping(target = "reviewImages", ignore = true)
    // @Mapping(target = "deletedAt", ignore = true)
    Review toEntity(ReviewCreateRequestDto requestDto, Camp camp, Reservation reservation);

    @Mapping(target = "reviewId", source = "id")
    @Mapping(target = "images", source = "reviewImages")
    ReviewResponseDto toResponseDto(Review review);

    // 리뷰 수정 매퍼
    @Mapping(target = "title", source = "requestDto.title", defaultValue = "review.title")
    @Mapping(target = "content", source = "requestDto.content", defaultValue = "review.content")
    Review updateFromRequest(Review review, ReviewUpdateRequestDto requestDto);

    List<ReviewResponseDto> toResponseDtoList(List<Review> reviews);

    @Mapping(target = "isRecommend", expression = "java(!review.isRecommend())")
    Review toUpdatedReview(Review review);
}
