package site.campingon.campingon.camp.repository.mongodb;

import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import site.campingon.campingon.camp.dto.mongodb.SearchResultDto;
import site.campingon.campingon.camp.entity.mongodb.SearchInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class MongoRecommendClient {
    private final MongoTemplate mongoTemplate;
    private static final String INDEX_NAME = "searchIndex";
    private static final String COLLECTION_NAME = "search_info";

    private static final String PROJECT_STAGE = "{" +
        "$project: {" +
        "camp_id: 1," +
        "name: 1," +
        "intro: 1," +
        "image_url: 1," +
        "address: 1," +
        "hashtags: 1," +
        "matchCount: 1," + // 매칭된 키워드 수 추가
        "score: {$meta: 'searchScore'}" +
        "}}";

    public SearchResultDto getMatchedCamps(List<String> userKeywords, Pageable pageable) {
        String searchQuery = String.format(
            "{$search: {" +
                "index: '%s'," +
                "compound: {" +
                "should: [%s]" +
                "}" +
                "}}",
            INDEX_NAME,
            createKeywordMatchClauses(userKeywords)
        );

        // 매칭된 키워드 수를 계산하는 스테이지
        String matchCountStage = "{$addFields: {" +
            "matchCount: {" +
                "$size: {" +
            "$setIntersection: [\"$hashtags\", " +
                    "[" + userKeywords.stream()
                    .map(keyword -> "\"" + keyword + "\"")
                    .collect(Collectors.joining(", ")) + "]" +
                    "]" +
                "}" +
            "}" +
        "}}";

        // 매칭된 키워드가 1개 이상인 것만 필터링
        String filterStage = "{$match: {matchCount: {$gt: 0}}}";

        // 매칭 수와 검색 점수로 정렬
        String sortStage = "{$sort: {matchCount: -1, score: -1}}";

        String facetStage = String.format(
            "{$facet: {" +
                "results: [{$skip: %d}, {$limit: %d}]," +
                "total: [{$count: 'count'}]" +
            "}}",
            pageable.getOffset(),
            pageable.getPageSize()
        );

        AggregationOperation searchOperation = context -> Document.parse(searchQuery);
        AggregationOperation matchCountOperation = context -> Document.parse(matchCountStage);
        AggregationOperation filterOperation = context -> Document.parse(filterStage);
        AggregationOperation sortOperation = context -> Document.parse(sortStage);
        AggregationOperation projectOperation = context -> Document.parse(PROJECT_STAGE);
        AggregationOperation facetOperation = context -> Document.parse(facetStage);

        AggregationResults<Document> results = mongoTemplate.aggregate(
            Aggregation.newAggregation(
                searchOperation,
                matchCountOperation,
                filterOperation,
                sortOperation,
                projectOperation,
                facetOperation
            ),
            COLLECTION_NAME,
            Document.class
        );

        return processResults(results);
    }

    private String createKeywordMatchClauses(List<String> userKeywords) {
        if (userKeywords == null || userKeywords.isEmpty()) {
            return "[]";
        }

        List<String> clauses = new ArrayList<>();

        // 첫 번째 키워드 - 가장 높은 가중치
        if (userKeywords.size() >= 1 && StringUtils.hasText(userKeywords.get(0))) {
            clauses.add(String.format("{text: {" +
                "query: '%s'," +
                "path: 'hashtags'," +
                "score: {boost: {value: 6.0}}" +
                "}}", userKeywords.get(0)));
        }

        // 두 번째 키워드
        if (userKeywords.size() >= 2 && StringUtils.hasText(userKeywords.get(1))) {
            clauses.add(String.format("{text: {" +
                "query: '%s'," +
                "path: 'hashtags'," +
                "score: {boost: {value: 5.0}}" +
                "}}", userKeywords.get(1)));
        }

        // 기본 가중치
        if (userKeywords.size() > 2) {
            List<String> remainingClauses = userKeywords.stream()
                .skip(2)  // 첫 두 개의 키워드는 건너뛰기
                .filter(StringUtils::hasText)
                .map(keyword ->
                    String.format("{text: {" +
                        "query: '%s'," +
                        "path: 'hashtags'," +
                        "score: {boost: {value: 4.0}}" +
                        "}}", keyword)
                )
                .collect(Collectors.toList());

            clauses.addAll(remainingClauses);
        }

        return String.join(",", clauses);
    }

    private SearchResultDto processResults(AggregationResults<Document> results) {
        Document result = results.getUniqueMappedResult();
        if (result == null) {
            return new SearchResultDto(Collections.emptyList(), 0L);
        }

        List<Document> resultDocs = (List<Document>) result.get("results");
        List<Document> totalDocs = (List<Document>) result.get("total");

        if (resultDocs == null) {
            return new SearchResultDto(Collections.emptyList(), 0L);
        }

        List<SearchInfo> searchResults = resultDocs.stream()
            .map(doc -> mongoTemplate.getConverter().read(SearchInfo.class, doc))
            .collect(Collectors.toList());

        // Integer를 Long으로 안전하게 변환
        long total = 0L;
        if (totalDocs != null && !totalDocs.isEmpty()) {
            Number count = totalDocs.get(0).get("count", Number.class);
            if (count != null) {
                total = count.longValue();
            }
        }

        return new SearchResultDto(searchResults, total);
    }

}
