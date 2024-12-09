package site.campingon.campingon.camp.dto.mongodb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.campingon.campingon.camp.entity.mongodb.SearchInfo;

import java.util.List;

@Getter
@AllArgsConstructor
public class SearchResultDto {
    private final List<SearchInfo> results;
    private final long total;
}
