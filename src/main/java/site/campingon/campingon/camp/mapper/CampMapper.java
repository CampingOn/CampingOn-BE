package site.campingon.campingon.camp.mapper;

import org.mapstruct.*;
import site.campingon.campingon.camp.dto.*;
import site.campingon.campingon.camp.dto.admin.CampUpdateRequestDto;
import site.campingon.campingon.camp.dto.admin.CampCreateRequestDto;
import site.campingon.campingon.camp.entity.*;

import java.util.List;

// 엔티티와 DTO 간 매핑 시 매핑되지 않은 필드가 있어도 MapStruct가 경고나 오류를 생성 x
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CampMapper {

  // Camp -> CampListResponseDto로 매핑
  @Mapping(target = "name", source = "campName")
  @Mapping(target = "keywords", source = "keywords", qualifiedByName = "keywordsToStringList")
  @Mapping(target = "address", source = "campAddr.streetAddr")
  CampListResponseDto toCampListDto(Camp camp);

  // Camp -> CampDetailResponseDto 매핑
  @Mapping(target = "name", source = "campName")
  @Mapping(target = "images", source = "images", qualifiedByName = "imagesToUrlList")
  CampDetailResponseDto toCampDetailDto(Camp camp);



  @Named("keywordsToStringList")
  default List<String> keywordsToStringList(List<CampKeyword> keywords) {
    return keywords.stream()
            .map(CampKeyword::getKeyword)
            .toList();
  }

  @Named("imagesToUrlList")
  default List<String> imagesToUrlList(List<CampImage> images) {
    if (images == null) return null;
    return images.stream()
        .map(CampImage::getImageUrl)
        .toList();
  }

  @Named("urlsToImagesList")
  default List<CampImage> urlsToImagesList(List<String> imageUrls) {
    if (imageUrls == null) return null;
    return imageUrls.stream()
        .map(url -> CampImage.builder()
            .imageUrl(url)
            .build())
        .toList();
  }

  // 업데이트 로직을 위한 메서드
  void updateCampFromDto(Camp updatedCamp, @MappingTarget Camp existingCamp);

  // CampCreateRequestDto -> Camp
  @Mapping(target = "campName", source = "campName")
  @Mapping(target = "images", source = "images", qualifiedByName = "urlsToImagesList")
  Camp toCampEntity(CampCreateRequestDto createRequestDto);

  // CampUpdateRequestDto -> Camp
  @Mapping(target = "campName", source = "campName")
  @Mapping(target = "images", source = "images", qualifiedByName = "urlsToImagesList")
  Camp toCampEntity(CampUpdateRequestDto updateRequestDto);
}
