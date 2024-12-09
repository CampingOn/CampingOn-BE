package site.campingon.campingon.camp.mapper;

import org.mapstruct.*;
import site.campingon.campingon.camp.dto.CampSiteResponseDto;
import site.campingon.campingon.camp.dto.CampSiteListResponseDto;
import site.campingon.campingon.camp.dto.admin.CampSiteUpdateRequestDto;
import site.campingon.campingon.camp.dto.admin.CampSiteCreateRequestDto;
import site.campingon.campingon.camp.entity.Camp;
import site.campingon.campingon.camp.entity.CampSite;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CampSiteMapper {

    @Mapping(target = "siteId", source = "id")
    @Mapping(target = "campSimpleDto.campId", source = "camp.id")
    @Mapping(target = "campSimpleDto.campName", source = "camp.campName")
    @Mapping(target = "campSimpleDto.city", source = "camp.campAddr.city")
    @Mapping(target = "campSimpleDto.state", source = "camp.campAddr.state")
    @Mapping(target = "campSimpleDto.streetAddr", source = "camp.campAddr.streetAddr")
    CampSiteResponseDto toCampSiteResponseDto(CampSite campSite);

    @Mapping(target = "siteId", source = "id")
    CampSiteListResponseDto toCampSiteListResponseDto(CampSite campSite);

    // CampSiteCreateRequestDto -> CampSite 엔티티 변환
    @Mapping(target = "camp", source = "camp") // Camp 객체를 직접 매핑
    CampSite toCampSite(CampSite source, @Context Camp camp);

    CampSite toCampSite(CampSiteCreateRequestDto dto, @Context Camp camp);

    // CampSiteUpdateRequestDto 를 사용하여 CampSite 수정
    void updateCampSiteFromDto(CampSiteUpdateRequestDto dto, @MappingTarget CampSite campSite);
}