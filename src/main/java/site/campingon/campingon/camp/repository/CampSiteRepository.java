package site.campingon.campingon.camp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.campingon.campingon.camp.entity.CampSite;

import java.util.List;

@Repository
public interface CampSiteRepository extends JpaRepository<CampSite, Long> {

    List<CampSite> findByCampId(Long campId);
}