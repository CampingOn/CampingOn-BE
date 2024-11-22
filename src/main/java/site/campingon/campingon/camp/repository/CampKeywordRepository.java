package site.campingon.campingon.camp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.campingon.campingon.camp.entity.CampKeyword;

import java.util.List;

@Repository
public interface CampKeywordRepository extends JpaRepository<CampKeyword, Long> {
    List<CampKeyword> findByCampId(Long id);
}