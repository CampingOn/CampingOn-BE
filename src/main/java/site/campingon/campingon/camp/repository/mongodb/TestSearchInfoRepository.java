//package site.campingon.campingon.camp.repository.mongodb;
//
//import lombok.RequiredArgsConstructor;
//import org.bson.Document;
//import org.springframework.data.mongodb.core.MongoTemplate;
//import org.springframework.data.mongodb.core.aggregation.*;
//import org.springframework.stereotype.Repository;
//import org.springframework.util.StringUtils;
//import site.campingon.campingon.camp.entity.mongodb.SearchInfo;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//@Repository
//@RequiredArgsConstructor
//public class TestSearchInfoRepository {
//    private final MongoTemplate mongoTemplate;
//
//    public List<SearchInfo> searchWithUserPreferences(String searchTerm, List<String> userKeywords, String city) {
//        Document searchStage = Document.parse("{" +
//            "$search: {" +
//            "index: 'searchIndex'," +
//            "compound: {" +
//            "should: " + createShouldClauses(searchTerm, userKeywords) + "," +
//            "must: [" +
//            "{text: {query: '" + city + "', path: 'address.city'}}" +
//            "]" +
//            "}" +
//            "}}");
//
//        Document projectStage = Document.parse("{" +
//            "$project: {" +
//            "camp_id: 1," +
//            "name: 1," +
//            "line_intro: 1," +
//            "image_url: 1," +
//            "streetAddr: '$address.street_addr'," +
//            "hashtags: 1," +
//            "score: {$meta: 'searchScore'}" +
//            "}}");
//
//        List<Document> pipeline = Arrays.asList(searchStage, projectStage);
//
//        return mongoTemplate.aggregate(Aggregation.newAggregation(
//            SearchInfo.class,
//            Arrays.asList(
//                new AggregationOperation() {
//                    @Override
//                    public Document toDocument(AggregationOperationContext context) {
//                        return searchStage;
//                    }
//                },
//                Aggregation.project()
//                    .and("camp_id").as("campId")
//                    .and("name").as("name")
//                    .and("line_intro").as("lineIntro")
//                    .and("image_url").as("imageUrl")
//                    .and("address.street_addr").as("streetAddr")
//                    .and("hashtags").as("hashtags")
//                    .andExpression("{$meta: 'searchScore'}").as("score")
//            )
//        ), "search_info", SearchInfo.class).getMappedResults();
//    }
//
//    private String createShouldClauses(String searchTerm, List<String> userKeywords) {
//        List<String> clauses = new ArrayList<>();
//
//        if (StringUtils.hasText(searchTerm)) {
//            clauses.add("{text: {query: '" + searchTerm + "', path: 'name', score: {boost: {value: 5}}, fuzzy: {maxEdits: 1}}}");
//            clauses.add("{text: {query: '" + searchTerm + "', path: 'hashtags', score: {boost: {value: 4}}, fuzzy: {maxEdits: 1}}}");
//            clauses.add("{text: {query: '" + searchTerm + "', path: 'address.state', score: {boost: {value: 3}}, fuzzy: {maxEdits: 1}}}");
//            clauses.add("{text: {query: '" + searchTerm + "', path: 'address.city', score: {boost: {value: 2}}, fuzzy: {maxEdits: 1}}}");
//            clauses.add("{text: {query: '" + searchTerm + "', path: 'intro', score: {boost: {value: 2}}, fuzzy: {maxEdits: 2}}}");
//        }
//
//        for (String keyword : userKeywords) {
//            clauses.add("{text: {query: '" + keyword + "', path: 'hashtags', score: {boost: {value: 2.5}}}}");
//        }
//
//        return "[" + String.join(",", clauses) + "]";
//    }
//
//    public List<Document> countSearchResults(String searchTerm, List<String> userKeywords, String city) {
//        Document searchStage = Document.parse("{" +
//            "$search: {" +
//            "index: 'searchIndex'," +
//            "compound: {" +
//            "should: " + createShouldClauses(searchTerm, userKeywords) + "," +
//            "must: [" +
//            "{text: {query: '" + city + "', path: 'address.city'}}" +
//            "]" +
//            "}" +
//            "}}");
//
//        Document countStage = Document.parse("{$count: 'total'}");
//
//        List<Document> pipeline = Arrays.asList(searchStage, countStage);
//
//        return mongoTemplate.aggregate(Aggregation.newAggregation(
//            SearchInfo.class,
//            Arrays.asList(
//                new AggregationOperation() {
//                    @Override
//                    public Document toDocument(AggregationOperationContext context) {
//                        return searchStage;
//                    }
//                },
//                new AggregationOperation() {
//                    @Override
//                    public Document toDocument(AggregationOperationContext context) {
//                        return countStage;
//                    }
//                }
//            )
//        ), "search_info", Document.class).getMappedResults();
//    }
//}
//
//
//
//
//
///*
//@Aggregation(pipeline = {
//    "{$search: {" +
//        "index: 'searchIndex'," +
//        "compound: {" +
//        "should: [" +
//        "{text: { query: ?0, path: 'name', score: { boost: { value: 5 } }, fuzzy: { maxEdits: 2 } }}," +
//        "{text: { query: ?0, path: 'hashtags', score: { boost: { value: 4 } }, fuzzy: { maxEdits: 1 } }}," +
//        "{text: { query: ?0, path: 'address.state', score: { boost: { value: 3 } }, fuzzy: { maxEdits: 1 } }}," +
//        "{text: { query: ?0, path: 'address.city', score: { boost: { value: 2 } }, fuzzy: { maxEdits: 1 } }}," +
//        "{text: { query: ?0, path: 'intro', score: { boost: { value: 1 } }, fuzzy: { maxEdits: 2 } }}," +
//        "{text: { query: ?1, path: 'hashtags', score: { boost: { value: 1.5 } } }}" +
//        "]," +
//        "must: [" +
//        "{text: { query: ?2, path: 'address.city' }}" +
//        "]" +
//        "}" +
//        "}}",
//    "{$project: {" +
//        "camp_id: 1," +
//        "name: 1," +
//        "intro: 1," +
//        "image_url: 1," +
//        "streetAddr: '$address.street_addr'," +
//        "hashtags: 1," +
//        "score: {$meta: 'searchScore'}" +
//        "}}"
//})
//List<SearchInfo> searchWithUserPreferences(String name, List<String> userKeywords, String city);
//
//
//// 전체 문서 수를 계산하기 위한 메서드
//@Aggregation(pipeline = {
//    "{$search: {" +
//        "index: 'searchIndex'," +
//        "compound: {" +
//        "should: [" +
//        "{text: { query: ?0, path: ['name', 'hashtags', 'address.state', 'address.city', 'intro'], fuzzy: { maxEdits: 1 } }}," +
//        "{text: { query: ?1, path: 'hashtags' }}" +
//        "]," +
//        "must: [" +
//        "{text: { query: ?2, path: 'address.city' }}" +
//        "]" +
//        "}" +
//        "}}",
//    "{$count: 'total'}"
//})
//List<Document> countSearchResults(String searchTerm, List<String> userKeywords, String city);*/
