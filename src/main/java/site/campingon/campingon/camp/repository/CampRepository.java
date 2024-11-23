package site.campingon.campingon.camp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.campingon.campingon.camp.entity.Camp;

import java.util.Collection;
import java.util.List;

@Repository
public interface CampRepository extends JpaRepository<Camp, Long> {

    @Query("""
    SELECT DISTINCT c FROM Camp c
    JOIN CampKeyword ck ON c.id = ck.camp.id
    WHERE ck.keyword IN :keywords
    GROUP BY c
    ORDER BY COUNT(ck.keyword) DESC
  """)
    Page<Camp> findRecommendedCampsByKeywords(@Param("keywords") List<String> keywords, Pageable pageable);

    @Query("""
    SELECT c FROM Camp c
    JOIN CampInfo ci ON c.id = ci.camp.id
    ORDER BY ci.recommendCnt DESC
  """)
    Page<Camp> findPopularCamps(Pageable pageable);  // 추천수 순 내림차순

}