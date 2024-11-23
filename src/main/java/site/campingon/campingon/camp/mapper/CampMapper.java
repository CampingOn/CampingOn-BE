package site.campingon.campingon.camp.mapper;

import org.mapstruct.*;
import site.campingon.campingon.camp.dto.*;
import site.campingon.campingon.camp.entity.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

// 엔티티와 DTO 간 매핑 시 매핑되지 않은 필드가 있어도 MapStruct가 경고나 오류를 생성 x
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CampMapper {

    // Camp -> CampListResponseDto로 매핑
    @Mapping(target = "name", source = "campName")
    @Mapping(target = "keywords", source = "keywords", qualifiedByName = "keywordsToStringList")
    @Mapping(target = "address", source = "campAddr", qualifiedByName = "addressToString")
    CampListResponseDto toCampListDto(Camp camp);

    @Mapping(target = "name", source = "campName")
    @Mapping(target = "address", source = "campAddr", qualifiedByName = "addressToString")
    @Mapping(target = "recommendCnt", source = "campInfo.recommendCnt")
    @Mapping(target = "likeCnt", source = "campInfo.likeCnt")
    CampDetailResponseDto toCampDetailDto(Camp camp);

    CampSiteListResponseDto toCampSiteListDto(CampSite campSite);

    @Named("addressToString")
    default String addressToString(CampAddr address) {
        return address != null ? address.getFullAddress() : null;
    }

    @Named("keywordsToStringList")
    default List<String> keywordsToStringList(List<CampKeyword> keywords) {
        return keywords.stream()
                .map(CampKeyword::getKeyword)
                .toList();
    }

}