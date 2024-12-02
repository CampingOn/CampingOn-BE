package site.campingon.campingon.camp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.campingon.campingon.camp.entity.Camp;

import java.util.List;

@Repository
public interface CampRepository extends JpaRepository<Camp, Long> {

  // 캠핑자과 사용자의 키워드가 일치하는 경우의 내림차순으로 캠핑장 정렬
  @Query("""
    SELECT DISTINCT c FROM Camp c
    JOIN CampKeyword ck ON c.id = ck.camp.id
    WHERE ck.keyword IN :keywords
    GROUP BY c
    ORDER BY COUNT(ck.keyword) DESC
  """)
  Page<Camp> findMatchedCampsByKeywords(@Param("keywords") List<String> keywords, Pageable pageable);

  // 캠핑장 정보의 추천수의 내림차순으로 캠핑장 정렬
  @Query("""
    SELECT c FROM Camp c
    JOIN CampInfo ci ON c.id = ci.camp.id
    ORDER BY ci.recommendCnt DESC
  """)
  Page<Camp> findPopularCamps(Pageable pageable);

  // 캠핑장명 검색
  @Query("""
    SELECT DISTINCT c FROM Camp c
    LEFT JOIN c.campAddr ca
    LEFT JOIN c.campInfo cr
    WHERE (:city IS NULL OR ca.city = :city)
    GROUP BY c
    ORDER BY cr.recommendCnt DESC
  """)
  Page<Camp> findByCampNameSearch(String city, Pageable pageable);

  // 캠핑 지역 확인 or 검색어와 일치하는 필드들(업종, 부대시설, 시군구, 캠핑장 키워드)의 캠핑장 목록 추천순으로 정렬
  @Query("""
    SELECT DISTINCT c FROM Camp c
    LEFT JOIN c.campAddr ca
    LEFT JOIN c.induty ci
    LEFT JOIN c.campInfo cr
    LEFT JOIN c.keywords ck
    WHERE (:city IS NULL OR ca.city = :city)
    AND (:keyword IS NULL OR (
        LOWER(ci.induty) = :keyword
        OR LOWER(c.outdoorFacility) = :keyword
        OR LOWER(ca.state) = :keyword
        OR LOWER(ck.keyword) = :keyword
    ))
    GROUP BY c
    ORDER BY cr.recommendCnt DESC
  """)
  Page<Camp> searchCampsByKeywordAndCity(
      @Param("keyword") String keyword, @Param("city") String city, Pageable pageable
  );


  // 사용자의 isMarked 된 캠핑장 목록 페이지
  Page<Camp> findByBookmarks_User_IdAndBookmarks_IsMarkedTrue(Long userId, Pageable pageable);

  //쿼리 최적화 where in 조건
  @Modifying
  @Query("DELETE FROM Camp c WHERE c.id IN :ids")
  int deleteByIds(@Param("ids") List<Long> ids);

}
