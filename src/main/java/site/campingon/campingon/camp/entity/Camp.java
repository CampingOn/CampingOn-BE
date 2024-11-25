package site.campingon.campingon.camp.entity;

import jakarta.persistence.*;
import lombok.*;
import site.campingon.campingon.bookmark.entity.Bookmark;
import site.campingon.campingon.common.entity.BaseEntity;

import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "camp")
public class Camp extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(columnDefinition = "INT UNSIGNED")
  private Long id;

  @Column(name = "camp_name", length = 50, nullable = false)
  private String campName;

  @Column(length = 20)
  private String tel;

  //    @Lob // MongoDB 호환성을 위해선 Lob 사용 권장
  @Column(name = "intro", columnDefinition = "TEXT")
  private String intro;

  @Column(name = "line_intro", length = 255)
  private String lineIntro; // 요약

  @Column(length = 100)
  private String homepage;

  @Column(length = 100, nullable = false)
  private String induty;  // 업종

  @Column(name = "outdoor_facility", length = 255)
  private String outdoorFacility;  // 부대 시설

  @Column(name = "thumb_image", length = 255)
  private String thumbImage;  // 썸네일 이미지

  @OneToMany(mappedBy = "camp", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<CampKeyword> keywords;

  @OneToOne(mappedBy = "camp", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private CampAddr campAddr;

  @OneToOne(mappedBy = "camp", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private CampInfo campInfo;

  @OneToMany(mappedBy = "camp", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Bookmark> bookmarks;

}
