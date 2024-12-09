package site.campingon.campingon.camp.mapper.mongodb;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import site.campingon.campingon.camp.dto.CampListResponseDto;
import site.campingon.campingon.camp.entity.mongodb.SearchInfo;

@Mapper(componentModel = "spring")
public interface SearchInfoMapper {

    @Mapping(source = "imageUrl", target = "thumbImage")
    @Mapping(source = "intro", target = "lineIntro")
    @Mapping(source = "address.streetAddr", target = "streetAddr")
    @Mapping(source = "hashtags", target = "keywords")
    @Mapping(target = "isMarked", constant = "false")
    @Mapping(target = "username", ignore = true)
    CampListResponseDto toDto(SearchInfo searchInfo);

    default Page<CampListResponseDto> toPageDto(Page<SearchInfo> page) {
        return page.map(this::toDto);
    }
}
