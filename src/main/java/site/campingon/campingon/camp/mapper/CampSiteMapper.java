package site.campingon.campingon.camp.mapper;

import org.mapstruct.*;
import site.campingon.campingon.camp.dto.CampSiteDetailResponseDto;
import site.campingon.campingon.camp.dto.CampSiteListResponseDto;
import site.campingon.campingon.camp.dto.admin.CreateCampSiteRequestDto;
import site.campingon.campingon.camp.dto.admin.UpdateCampSiteRequestDto;
import site.campingon.campingon.camp.entity.Camp;
import site.campingon.campingon.camp.entity.CampSite;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CampSiteMapper {

    CampSiteDetailResponseDto toCampSiteDetailResponseDto(CampSite campSite);

    CampSiteListResponseDto toCampSiteListResponseDto(CampSite campSite);

    // CreateCampSiteRequestDto -> CampSite 엔티티 변환
    @Mapping(target = "camp", source = "camp") // Camp 객체를 직접 매핑
    CampSite toCampSite(CampSite source, @Context Camp camp);

    CampSite toCampSite(CreateCampSiteRequestDto dto, @Context Camp camp);

    // UpdateCampSiteRequestDto 를 사용하여 CampSite 수정
    void updateCampSiteFromDto(UpdateCampSiteRequestDto dto, @MappingTarget CampSite campSite);
}