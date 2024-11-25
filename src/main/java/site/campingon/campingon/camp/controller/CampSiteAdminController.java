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
import site.campingon.campingon.camp.mapper.CampSiteMapper;
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
        CampSiteResponseDto responseDto = campSiteService.createCampSite(campId, createRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // 캠핑지 수정
    @PutMapping("/{siteId}")
    public ResponseEntity<CampSiteResponseDto> updateCampSite(
            @PathVariable("campId") Long campId,
            @PathVariable("siteId") Long siteId,
            @RequestBody @Valid CampSiteUpdateRequestDto updateRequestDto) {
        CampSiteResponseDto responseDto = campSiteService.updateCampSite(campId, siteId, updateRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    // 캠핑지 삭제
    @DeleteMapping("/{siteId}")
    public ResponseEntity<Void> deleteCampSite(
            @PathVariable("campId") Long campId,
            @PathVariable("siteId") Long siteId) {
        campSiteService.deleteCampSite(campId, siteId);
        return ResponseEntity.noContent().build();
    }

    // 특정 캠핑장의 모든 캠핑지 조회
    @GetMapping
    public ResponseEntity<List<CampSiteListResponseDto>> getCampSites(
            @PathVariable("campId") Long campId) {
        List<CampSiteListResponseDto> responseDtos = campSiteService.getCampSites(campId);
        return ResponseEntity.ok(responseDtos);
    }

    // 특정 캠핑지 조회
    @GetMapping("/{siteId}")
    public ResponseEntity<CampSiteResponseDto> getCampSite(
            @PathVariable("campId") Long campId,
            @PathVariable("siteId") Long siteId) {
        CampSiteResponseDto responseDto = campSiteService.getCampSite(campId, siteId);
        return ResponseEntity.ok(responseDto);
    }
}
