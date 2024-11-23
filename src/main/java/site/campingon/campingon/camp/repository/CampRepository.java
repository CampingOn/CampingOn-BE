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
    SELECT c FROM Camp c
    JOIN CampKeyword ck ON c.id = ck.camp.id
    WHERE ck.keyword IN :keywords
    GROUP BY c
    ORDER BY COUNT(ck.keyword) DESC
  """)
    List<Camp> findRecommendedCampsByKeywords(@Param("keywords") List<String> keywords, Pageable pageable);

    @Query("""
    SELECT c FROM Camp c
    JOIN CampInfo ci ON c.id = ci.camp.id
    ORDER BY ci.recommendCnt DESC
  """)
    Page<Camp> findPopularCamps(Pageable pageable);  // 추천수 순 내림차순

    @Query(value = "SELECT * FROM camp c " +
            "JOIN camp_info ci ON c.id = ci.camp_id " +
            "WHERE c.user_id = :userId " +
            "ORDER BY ci.recommend_cnt DESC " +
            "LIMIT :size", nativeQuery = true)
    List<Camp> findRecommendedCampsByUserId(@Param("userId") Long userId, @Param("size") int size);


    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Like l WHERE l.camp.id = :campId AND l.user.id = :userId")
    boolean isLikedByUser(@Param("campId") Long campId, @Param("userId") Long userId);



    @Query("SELECT c FROM Camp c " +
            "WHERE (c.campName LIKE %:keyword% OR c.lineIntro LIKE %:keyword%) " +
            "AND c.campAddr.city LIKE %:location%")
    Page<Camp> searchCamps(@Param("keyword") String keyword, @Param("location") String location, PageRequest pageRequest);
}