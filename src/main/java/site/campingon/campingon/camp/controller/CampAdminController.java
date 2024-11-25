package site.campingon.campingon.camp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.campingon.campingon.camp.dto.CampDetailResponseDto;
import site.campingon.campingon.camp.dto.CampListResponseDto;
import site.campingon.campingon.camp.dto.admin.CampUpdateRequestDto;
import site.campingon.campingon.camp.dto.admin.CampCreateRequestDto;
import site.campingon.campingon.camp.entity.Camp;
import site.campingon.campingon.camp.service.CampService;
import site.campingon.campingon.camp.mapper.CampMapper;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/admin/camps")
@RequiredArgsConstructor
public class CampAdminController {

    private final CampService campService;
    private final CampMapper campMapper;

    // 캠핑장 생성
    @PostMapping
    public ResponseEntity<CampDetailResponseDto> createCamp(
            @RequestBody @Valid CampCreateRequestDto campCreateRequestDto) {
        Camp camp = campMapper.toCampEntity(campCreateRequestDto); // 요청 DTO -> 엔티티 변환
        return ResponseEntity.created(null).body(campService.createCamp(camp));
    }

    // 캠핑장 수정
    @PutMapping("/{campId}")
    public ResponseEntity<CampDetailResponseDto> updateCamp(
            @PathVariable("campId") Long campId,
            @RequestBody @Valid CampUpdateRequestDto campUpdateRequestDto
    ) {
        Camp updatedCamp = campMapper.toCampEntity(campUpdateRequestDto); // 요청 DTO -> 엔티티 변환
        return ResponseEntity.ok(campService.updateCamp(campId, updatedCamp));
    }

    // 캠핑장 삭제
    @DeleteMapping("/{campId}")
    public ResponseEntity<Void> deleteCamp(
            @PathVariable("campId") Long campId) {
        campService.deleteCamp(campId);
        return ResponseEntity.noContent().build();
    }

    // 모든 캠핑장 조회
    @GetMapping
    public ResponseEntity<List<CampListResponseDto>> getAllCamps() {
        List<CampListResponseDto> responseDtos = campService.getAllCamps();
        return ResponseEntity.ok(responseDtos);
    }
}