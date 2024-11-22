package site.campingon.campingon.camp.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.campingon.campingon.camp.entity.Camp;

import java.util.List;

@Repository
public interface CampRepository extends JpaRepository<Camp, Long> {
    List<Camp> findRecommendedCampsByUserId(Long userId, int size);

    boolean isLikedByUser(Long id, Long userId);

    Page<Camp> findPopularCamps(PageRequest pageRequest);

    Page<Camp> searchCamps(String keyword, String location, PageRequest pageRequest);
}