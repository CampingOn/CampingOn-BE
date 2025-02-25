package site.campingon.campingon.camp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.campingon.campingon.camp.entity.CampInfo;

import java.util.Optional;

@Repository
public interface CampInfoRepository extends JpaRepository<CampInfo, Long> {
    Optional<CampInfo> findByCampId(Long id);
}