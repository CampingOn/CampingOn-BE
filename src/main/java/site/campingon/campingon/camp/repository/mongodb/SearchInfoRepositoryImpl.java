package site.campingon.campingon.camp.repository.mongodb;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;
import site.campingon.campingon.camp.entity.mongodb.SearchInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SearchInfoRepositoryImpl implements SearchInfoRepository {
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
            "score: {$meta: 'searchScore'}" +
        "}}";

    @Override
    public SearchResult searchWithUserPreferences(String searchTerm, List<String> userKeywords, String city, Pageable pageable) {
        String mustClause = StringUtils.hasText(city) ?
            ", must: [{text: {query: '" + city + "', path: 'address.city'}}]" :
            "";

        String searchQuery = String.format(
            "{$search: {" +
                "index: 'searchIndex'," +
                "compound: {" +
                    "should: %s" +
                        "%s" +  // must clause를 조건부로 추가
                    "}" +
                "}}",
            createShouldClauses(searchTerm, userKeywords),
            mustClause
        );

        String facetStage = String.format(
            "{$facet: {" +
                    "results: [{$skip: %d}, {$limit: %d}]," +
                    "total: [{$count: 'count'}]" +
                "}}",
            pageable.getOffset(),
            pageable.getPageSize()
        );

        AggregationOperation searchOperation = context -> Document.parse(searchQuery);
        AggregationOperation projectOperation = context -> Document.parse(PROJECT_STAGE);
        AggregationOperation facetOperation = context -> Document.parse(facetStage);

        AggregationResults<Document> results = mongoTemplate.aggregate(
            Aggregation.newAggregation(
                searchOperation,
                projectOperation,
                facetOperation
            ),
            COLLECTION_NAME,
            Document.class
        );

        return processResults(results);
    }

    private String createShouldClauses(String searchTerm, List<String> userKeywords) {
        List<String> clauses = new ArrayList<>();

        if (StringUtils.hasText(searchTerm)) {
            clauses.add("{text: {query: '" + searchTerm + "', path: 'name', score: {boost: {value: 5}}, fuzzy: {maxEdits: 1}}}");
            clauses.add("{text: {query: '" + searchTerm + "', path: 'hashtags', score: {boost: {value: 4}}, fuzzy: {maxEdits: 1}}}");
            clauses.add("{text: {query: '" + searchTerm + "', path: 'address.state', score: {boost: {value: 3}}, fuzzy: {maxEdits: 1}}}");
            clauses.add("{text: {query: '" + searchTerm + "', path: 'address.city', score: {boost: {value: 2}}, fuzzy: {maxEdits: 1}}}");
            clauses.add("{text: {query: '" + searchTerm + "', path: 'intro', score: {boost: {value: 2}}, fuzzy: {maxEdits: 2}}}");
        }

        if (userKeywords != null && !userKeywords.isEmpty()) {
            for (String keyword : userKeywords) {
                if (StringUtils.hasText(keyword)) {
                    clauses.add("{text: {query: '" + keyword + "', path: 'hashtags', score: {boost: {value: 2.5}}}}");
                }
            }
        }

        return clauses.isEmpty() ? "[]" : "[" + String.join(",", clauses) + "]";
    }

    @Getter
    @AllArgsConstructor
    public static class SearchResult {
        private final List<SearchInfo> results;
        private final long total;
    }

    private SearchResult processResults(AggregationResults<Document> results) {
        Document result = results.getUniqueMappedResult();
        if (result == null) {
            return new SearchResult(Collections.emptyList(), 0L);
        }

        List<Document> resultDocs = (List<Document>) result.get("results");
        List<Document> totalDocs = (List<Document>) result.get("total");

        if (resultDocs == null) {
            return new SearchResult(Collections.emptyList(), 0L);
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

        return new SearchResult(searchResults, total);
    }
}
