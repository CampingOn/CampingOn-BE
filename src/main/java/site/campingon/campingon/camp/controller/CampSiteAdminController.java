package site.campingon.campingon.camp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.campingon.campingon.camp.dto.CampSiteResponseDto;
import site.campingon.campingon.camp.dto.CampSiteListResponseDto;
import site.campingon.campingon.camp.dto.admin.CampSiteCreateRequestDto;
import site.campingon.campingon.camp.dto.admin.CampSiteUpdateRequestDto;
import site.campingon.campingon.camp.service.CampSiteService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/camps/{campId}/sites")
@RequiredArgsConstructor
public class CampSiteAdminController {

    private final CampSiteService campSiteService;

    // 캠핑지 생성
    @PostMapping
    public ResponseEntity<CampSiteResponseDto> createCampSite(
            @PathVariable("campId") Long campId,
            @RequestBody @Valid CampSiteCreateRequestDto createRequestDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(campSiteService.createCampSite(campId, createRequestDto));
    }

    // 캠핑장의 모든 캠핑지 조회
    @GetMapping
    public ResponseEntity<List<CampSiteListResponseDto>> getCampSites(
            @PathVariable("campId") Long campId) {

        return ResponseEntity.ok(campSiteService.getCampSites(campId));
    }

    // 특정 캠핑지 조회
    @GetMapping("/{siteId}")
    public ResponseEntity<CampSiteResponseDto> getCampSite(
            @PathVariable("campId") Long campId,
            @PathVariable("siteId") Long siteId) {

        return ResponseEntity.ok(campSiteService.getCampSite(campId, siteId));
    }

    // 캠핑지 수정
    @PutMapping("/{siteId}")
    public ResponseEntity<CampSiteResponseDto> updateCampSite(
            @PathVariable("campId") Long campId,
            @PathVariable("siteId") Long siteId,
            @RequestBody @Valid CampSiteUpdateRequestDto updateRequestDto) {

        return ResponseEntity.ok(campSiteService.updateCampSite(campId, siteId, updateRequestDto));
    }

    // 캠핑지 삭제
    @DeleteMapping("/{siteId}")
    public ResponseEntity<Void> deleteCampSite(
            @PathVariable("campId") Long campId,
            @PathVariable("siteId") Long siteId) {

        campSiteService.deleteCampSite(campId, siteId);

        return ResponseEntity.noContent().build();
    }

    // 특정 캠핑지의 isAvailable 상태를 토글 - 변경된 isAvailable 상태 반환
    @PutMapping("/{siteId}/available")
    public ResponseEntity<Boolean> toggleAvailability(
            @PathVariable("campId") Long campId,
            @PathVariable("siteId") Long siteId) {

        return ResponseEntity.ok(campSiteService.toggleAvailability(campId, siteId));
    }

    // TODO: 관리자 UI를 생각했을 때 특정 캠핑지의 상태만 조회할 일이 있을까? 안 쓰는 API 같다.
    // 특정 캠핑지의 isAvailable 상태 조회 - 현재 isAvailable 상태 반환
    @GetMapping("/{SiteId}/available")
    public ResponseEntity<Boolean> getAvailability(
            @PathVariable("campId") Long campId,
            @PathVariable("SiteId") Long siteId) {

        return ResponseEntity.ok(campSiteService.getAvailability(campId, siteId));
    }
}
