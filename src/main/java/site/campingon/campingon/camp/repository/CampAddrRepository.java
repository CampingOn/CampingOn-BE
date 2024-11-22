package site.campingon.campingon.camp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.campingon.campingon.camp.entity.CampAddr;

@Repository
public interface CampAddrRepository extends JpaRepository<CampAddr, Long> {
    CampAddr findByCampId(Long id);
}