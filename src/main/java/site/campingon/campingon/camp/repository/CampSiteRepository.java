package site.campingon.campingon.camp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import site.campingon.campingon.camp.entity.Camp;
import site.campingon.campingon.camp.entity.CampSite;
import site.campingon.campingon.camp.entity.Induty;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CampSiteRepository extends JpaRepository<CampSite, Long> {

  @Query("""
            SELECT cs FROM CampSite cs
            WHERE cs.camp.id = :campId
            """)
  List<CampSite> findByCampId(@Param("campId") Long campId);

  @Query("""
            SELECT cs FROM CampSite cs
            WHERE cs.id = :siteId
            AND cs.camp.id = :campId
            """)
  Optional<CampSite> findByIdAndCampId(@Param("siteId") Long siteId, @Param("campId") Long campId);

  //TODO: 27일 오전 스크럼 시간에 soft, hard delete 여부 논의하기
  void deleteByIdAndCampId(Long siteId, Long campId);

  //TODO: 캠프 컨트롤러와 캠프 어드민 컨트롤러에서 캠핑장의 모든 캠핑지를 조회하는 중복 로직이 존재함

  // 특정 캠핑장에 속한 모든 캠핑지 조회
  @Query("""
            SELECT cs FROM CampSite cs
            WHERE cs.camp.id = :campId""")
  List<CampSite> findAllByCampId(@Param("campId") Long campId);

  List<CampSite> findAllByCampAndSiteType(Camp camp, Induty induty);

  // 특정 캠핑장의 특정 날짜에 예약 가능한 캠프사이트를 타입별로 1개 반환되게끔 조회
  @Query("SELECT cs FROM CampSite cs " +
          "LEFT JOIN Reservation r ON cs.id = r.campSite.id " +
          "WHERE cs.isAvailable = true " +
          "AND cs.camp.id = :campId " +
          "AND (r.id IS NULL OR r.status = 'CANCELED') " + // 예약테이블에 없거나 취소상태여야 함
          "AND (r.checkinDate < :checkout AND r.checkoutDate > :checkin) " +
          "ORDER BY cs.id ASC")
  List<CampSite> findAvailableCampSites(@Param("campId") Long campId,
                                        @Param("checkin") LocalDate checkin,
                                        @Param("checkout") LocalDate checkout);
}