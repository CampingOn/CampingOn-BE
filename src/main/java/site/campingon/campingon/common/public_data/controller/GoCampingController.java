package site.campingon.campingon.common.public_data.controller;

import org.locationtech.jts.io.ParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import site.campingon.campingon.common.public_data.GoCampingPath;
import site.campingon.campingon.common.public_data.dto.GoCampingDataDto;
import site.campingon.campingon.common.public_data.dto.GoCampingImageDto;
import site.campingon.campingon.common.public_data.dto.GoCampingImageParsedResponseDto;
import site.campingon.campingon.common.public_data.dto.GoCampingParsedResponseDto;
import site.campingon.campingon.common.public_data.service.GoCampingService;

import java.net.URISyntaxException;
import java.util.List;

/**
 * https://www.data.go.kr/tcs/dss/selectApiDataDetailView.do?publicDataPk=15101933
 * 공공데이터 고캠핑 정보 기반으로 캠프관련 엔티티 생성
 */
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class GoCampingController {

    private final GoCampingService goCampingService;

    //공공데이터 기반 캠프관련 엔티티 생성 및 DB 저장
    @PostMapping("/basedList")
    public ResponseEntity<List<GoCampingParsedResponseDto>> createCampByGoCampingBasedList(
            @RequestParam("numOfRows") Long numOfRows,  //한 페이지 결과 수
            @RequestParam("pageNo") Long pageNo)    //현재 페이지 번호
            throws URISyntaxException, ParseException {
        //공공데이터를 조회하고 반환
        GoCampingDataDto goCampingDataDto = goCampingService.getAndConvertToGoCampingDataDto(
                GoCampingPath.BASED_LIST,
                "numOfRows", numOfRows.toString(),
                "pageNo", pageNo.toString());

        //Camp 관련 엔티티를 생성하고 DB에 저장한다.
        List<GoCampingParsedResponseDto> goCampingParsedResponseDtos
                = goCampingService.createCampByGoCampingData(goCampingDataDto);

        return ResponseEntity.status(HttpStatus.OK).body(goCampingParsedResponseDtos);
    }

    /**
     * DB에 Camp Id를 가져와서 Id가 가지고있는 이미지를 호출 및 저장
     * */
    @PostMapping("/imageList")
    public ResponseEntity<List<List<GoCampingImageParsedResponseDto>>> createCampImageByGoCampingImageList(
            @RequestParam("imageCnt") Long imageCnt)    //몇개의 이미지개수를 갖고올지
            throws URISyntaxException {
        //공공데이터를 조회하고 dto로 변환
        List<GoCampingImageDto> goCampingImageDto = goCampingService.getAndConvertToGoCampingImageDataDto(imageCnt);

        //CampImage 를 생성하고 DB에 저장한다.
        List<List<GoCampingImageParsedResponseDto>> goCampingParsedResponseDtos
                = goCampingService.createOrUpdateCampImageByGoCampingImageData(goCampingImageDto);

        return ResponseEntity.status(HttpStatus.OK).body(goCampingParsedResponseDtos);
    }

//    //위치기반정보 목록 조회
//    @GetMapping("/locationBasedList")
//    public ResponseEntity<List<GoCampingParsedResponseDto>> GetGoCampingLocationBasedList(
//            @RequestParam("numOfRows") Long numOfRows,
//            @RequestParam("pageNo") Long pageNo,
//            @RequestParam("mapX") String mapX,
//            @RequestParam("mapY") String mapY,
//            @RequestParam("radius") String radius)
//            throws URISyntaxException {
//        GoCampingDataDto goCampingDataDto = goCampingService.goCampingDataDtoByGoCampingUrl(
//                GoCampingPath.LOCATION_BASED_LIST,
//                "numOfRows", numOfRows.toString(),
//                "pageNo", pageNo.toString(),
//                "mapX", mapX, "mapY", mapY, "radius", radius);
//
//        List<GoCampingParsedResponseDto> goCampingParsedResponseDtos
//                = goCampingService.createCampByGoCampingData(goCampingDataDto);
//
//        return ResponseEntity.status(HttpStatus.OK).body(goCampingParsedResponseDtos);
//    }

//    //키워드 검색 목록 조회
//    @GetMapping("/searchList")
//    public ResponseEntity<List<GoCampingParsedResponseDto>> GetGoCampingKeywordList(
//            @RequestParam("numOfRows") Long numOfRows,
//            @RequestParam("pageNo") Long pageNo,
//            @RequestParam("keyword") String keyword)
//            throws URISyntaxException {
//        String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8); //한글 인코딩
//        GoCampingDataDto goCampingDataDto = goCampingService.goCampingDataDtoByGoCampingUrl(
//                GoCampingPath.SEARCH_LIST,
//                "numOfRows", numOfRows.toString(),
//                "pageNo", pageNo.toString(),
//                "keyword", encodedKeyword);
//
//        List<GoCampingParsedResponseDto> goCampingParsedResponseDtos
//                = goCampingService.createCampByGoCampingData(goCampingDataDto);
//
//        return ResponseEntity.status(HttpStatus.OK).body(goCampingParsedResponseDtos);
//    }

//    //동기화 목록 조회
//    @GetMapping("/basedSyncList")
//    public ResponseEntity<List<GoCampingParsedResponseDto>> GetGoCampingBasedSyncList(
//            @RequestParam("numOfRows") Long numOfRows,
//            @RequestParam("pageNo") Long pageNo,
//            @RequestParam("syncStatus") String syncStatus)
//            throws URISyntaxException {
//        GoCampingDataDto goCampingDataDto = goCampingService.goCampingDataDtoByGoCampingUrl(
//                GoCampingPath.BASED_SYNC_LIST,
//                "numOfRows", numOfRows.toString(),
//                "pageNo", pageNo.toString(),
//                "syncStatus", syncStatus);
//
//        List<GoCampingParsedResponseDto> goCampingParsedResponseDtos = goCampingService.createCampByGoCampingData(goCampingDataDto);
//
//        return ResponseEntity.status(HttpStatus.OK).body(goCampingParsedResponseDtos);
//    }
}
