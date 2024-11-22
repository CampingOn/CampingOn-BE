package site.campingon.campingon.camp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import site.campingon.campingon.camp.entity.CampImage;

import java.util.List;

@Repository
public interface CampImageRepository extends JpaRepository<CampImage, Long> {
    List<CampImage> findByCampId(Long id);
}