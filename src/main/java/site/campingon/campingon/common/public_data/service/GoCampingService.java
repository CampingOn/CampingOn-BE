package site.campingon.campingon.common.public_data.service;

import org.locationtech.jts.io.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import site.campingon.campingon.camp.entity.*;
import site.campingon.campingon.camp.repository.*;
import site.campingon.campingon.common.exception.ErrorCode;
import site.campingon.campingon.common.exception.GlobalException;
import site.campingon.campingon.common.public_data.GoCampingPath;
import site.campingon.campingon.common.public_data.dto.GoCampingDataDto;
import site.campingon.campingon.common.public_data.dto.GoCampingImageDto;
import site.campingon.campingon.common.public_data.dto.GoCampingImageParsedResponseDto;
import site.campingon.campingon.common.public_data.dto.GoCampingParsedResponseDto;
import site.campingon.campingon.common.public_data.mapper.GoCampingMapper;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static site.campingon.campingon.camp.entity.Induty.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoCampingService {

    private final GoCampingMapper goCampingMapper;
    private final CampAddrRepository campAddrRepository;
    private final CampImageRepository campImageRepository;
    private final CampRepository campRepository;
    private final RestTemplate restTemplate;
    private final GoCampingProviderService goCampingProviderService;
    private static final String IMAGE_PAGE_NO = "1";    //이미지 몇번부터 값 꺼내올지

    //Camp 관련 엔티티 생성 및 DB 저장 메서드
    @Transactional
    public List<GoCampingParsedResponseDto> createCampByGoCampingData(GoCampingDataDto goCampingDataDto) {
        List<GoCampingDataDto.Item> items = goCampingDataDto.getResponse().getBody().getItems().getItem();
        List<GoCampingParsedResponseDto> goCampingParsedResponseDtoList = goCampingMapper.toGoCampingParsedResponseDtoList(items);

        for (GoCampingParsedResponseDto data : goCampingParsedResponseDtoList) {
            Integer normalSiteCnt = data.getGnrlSiteCo();//주요시설 일반야영장
            Integer carSiteCnt = data.getAutoSiteCo();//주요시설 자동차야영장
            Integer glampSiteCnt = data.getGlampSiteCo();//주요시설 글램핑
            Integer caravSiteCnt = data.getCaravSiteCo();//주요시설 카라반
            Integer personalCaravanSiteCnt = data.getIndvdlCaravSiteCo();//주요시설 개인 카라반
            String glampInnerFacility = data.getGlampInnerFclty();//글램핑 - 내부시설
            String caravInnerFacility = data.getCaravInnerFclty();//카라반 - 내부시설

            //데이터에 주요시설이 단, 한개도 없는 경우 DB 생성하지않는다.
            if (normalSiteCnt + carSiteCnt
                    + glampSiteCnt + caravSiteCnt
                    + personalCaravanSiteCnt == 0) {
                continue;
            }

            Camp camp = Camp.builder()
                    .id(data.getContentId())
                    .campName(data.getFacltNm())
                    .lineIntro(data.getLineIntro())
                    .intro(data.getIntro())
                    .tel(data.getTel())
                    .homepage(data.getHomepage())
                    .outdoorFacility(data.getSbrsCl())
                    .thumbImage(data.getFirstImageUrl())
                    .createdAt(
                            LocalDateTime.parse(
                                    data.getCreatedtime()
                                    , DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    )
                    .modifiedAt(LocalDateTime.parse(
                            data.getModifiedtime()
                            , DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    )
                    .build();

            campRepository.save(camp);

            goCampingProviderService.createOrUpdateCampInduty(camp, normalSiteCnt, carSiteCnt, glampSiteCnt, caravSiteCnt, personalCaravanSiteCnt);

            String pointWKT = String.format("POINT(%f %f)", data.getMapY(), data.getMapX());

            //create
            campAddrRepository.saveWithPoint(
                    camp.getId(),
                    data.getDoNm(),
                    data.getSigunguNm(),
                    data.getZipcode(),
                    data.getAddr1(),
                    data.getAddr2(),
                    pointWKT
            );

            //캠핑지 DB 저장
            goCampingProviderService.createCampSite(camp, normalSiteCnt, NORMAL_SITE, null,
                    NORMAL_SITE.getMaximum_people(), NORMAL_SITE.getPrice());

            goCampingProviderService.createCampSite(camp, carSiteCnt, CAR_SITE, null,
                    CAR_SITE.getMaximum_people(), CAR_SITE.getPrice());

            goCampingProviderService.createCampSite(camp, glampSiteCnt, GLAMP_SITE, glampInnerFacility,
                    GLAMP_SITE.getMaximum_people(), GLAMP_SITE.getPrice());

            goCampingProviderService.createCampSite(camp, caravSiteCnt, CARAV_SITE, caravInnerFacility,
                    CAR_SITE.getMaximum_people(), CAR_SITE.getPrice());

            goCampingProviderService.createCampSite(camp, personalCaravanSiteCnt, PERSONAL_CARAV_SITE,
                    null, PERSONAL_CARAV_SITE.getMaximum_people(), PERSONAL_CARAV_SITE.getPrice());


        }
        return goCampingParsedResponseDtoList;
    }

    //공공데이터 전체 API 조회하고 dto 변환
    public GoCampingDataDto getAndConvertToGoCampingDataDto(
            GoCampingPath goCampingPath,
            String... params
    ) throws URISyntaxException {
        URI uri = goCampingProviderService.createUri(goCampingPath, params);

        return restTemplate.getForObject(uri, GoCampingDataDto.class);  //API 호출
    }

    //공공데이터 이미지 API 조회하고 dto 변환
    public List<GoCampingImageDto> getAndConvertToGoCampingImageDataDto(
            Long imageCnt)
            throws URISyntaxException {
        List<GoCampingImageDto> goCampingDataDtoList = new ArrayList<>();

        List<Long> campIdList = campRepository.findAll().stream()
                .map(Camp::getId)
                .toList();

        for (Long campId : campIdList) {
            URI uri = goCampingProviderService.createUri(GoCampingPath.IMAGE_LIST,
                    "numOfRows", imageCnt.toString(),
                    "pageNo", IMAGE_PAGE_NO,  //몇번부터 시작할지
                    "contentId", campId.toString());

            goCampingDataDtoList.add(
                    restTemplate.getForObject(uri, GoCampingImageDto.class)); //API 호출
        }
        return goCampingDataDtoList;
    }

    //CampImage 를 생성 및 DB 저장 메서드
    @Transactional
    public List<List<GoCampingImageParsedResponseDto>> createOrUpdateCampImageByGoCampingImageData(
            List<GoCampingImageDto> goCampingImageDto) {
        List<List<GoCampingImageParsedResponseDto>> goCampingImageParsedResponseDtoList = new ArrayList<>();

        for (GoCampingImageDto goCampingDataDto : goCampingImageDto) {
            List<GoCampingImageDto.Item> item
                    = goCampingDataDto.getResponse().getBody().getItems().getItem();

            List<GoCampingImageParsedResponseDto> goCampingImageParsedResponseDto =
                    goCampingMapper.toGoCampingImageParsedResponseDtoList(item);

            Camp camp = campRepository.findById(
                            goCampingImageParsedResponseDto.getFirst().getContentId()
                    )
                    .orElseThrow(() -> new GlobalException(ErrorCode.CAMP_NOT_FOUND_BY_ID));
            for (GoCampingImageParsedResponseDto data : goCampingImageParsedResponseDto) {
                CampImage campImage = CampImage.builder()
                        .id(data.getSerialnum())
                        .camp(camp)
                        .imageUrl(data.getImageUrl())
                        .build();

                campImageRepository.save(campImage);
            }
            goCampingImageParsedResponseDtoList.add(goCampingImageParsedResponseDto);
        }
        return goCampingImageParsedResponseDtoList;
    }

    @Transactional
    public List<GoCampingParsedResponseDto> updateCampByGoCampingData(GoCampingDataDto goCampingDataDto) {
        List<GoCampingDataDto.Item> items = goCampingDataDto.getResponse().getBody().getItems().getItem();
        List<GoCampingParsedResponseDto> goCampingParsedResponseDtoList = goCampingMapper.toGoCampingParsedResponseDtoList(items);

        for (GoCampingParsedResponseDto data : goCampingParsedResponseDtoList) {
            Integer normalSiteCnt = data.getGnrlSiteCo();//주요시설 일반야영장
            Integer carSiteCnt = data.getAutoSiteCo();//주요시설 자동차야영장
            Integer glampSiteCnt = data.getGlampSiteCo();//주요시설 글램핑
            Integer caravSiteCnt = data.getCaravSiteCo();//주요시설 카라반
            Integer personalCaravanSiteCnt = data.getIndvdlCaravSiteCo();//주요시설 개인 카라반
            String glampInnerFacility = data.getGlampInnerFclty();//글램핑 - 내부시설
            String caravInnerFacility = data.getCaravInnerFclty();//카라반 - 내부시설

            //데이터에 주요시설이 단, 한개도 없는 경우 DB 생성하지않는다.
            if (normalSiteCnt + carSiteCnt
                    + glampSiteCnt + caravSiteCnt
                    + personalCaravanSiteCnt == 0) {
                continue;
            }

            Camp camp = Camp.builder()
                    .id(data.getContentId())
                    .campName(data.getFacltNm())
                    .lineIntro(data.getLineIntro())
                    .intro(data.getIntro())
                    .tel(data.getTel())
                    .homepage(data.getHomepage())
                    .outdoorFacility(data.getSbrsCl())
                    .thumbImage(data.getFirstImageUrl())
                    .createdAt(
                            LocalDateTime.parse(
                                    data.getCreatedtime()
                                    , DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    )
                    .modifiedAt(LocalDateTime.parse(
                            data.getModifiedtime()
                            , DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    )
                    .build();

            campRepository.save(camp);

            goCampingProviderService.createOrUpdateCampInduty(camp, normalSiteCnt, carSiteCnt, glampSiteCnt, caravSiteCnt, personalCaravanSiteCnt);

            String pointWKT = String.format("POINT(%f %f)", data.getMapY(), data.getMapX());

            campAddrRepository.updateWithPoint(
                    camp.getId(),
                    data.getDoNm(),
                    data.getSigunguNm(),
                    data.getZipcode(),
                    data.getAddr1(),
                    data.getAddr2(),
                    pointWKT
            );

            goCampingProviderService.updateCampSite(camp, normalSiteCnt, NORMAL_SITE, null,
                    NORMAL_SITE.getMaximum_people(), NORMAL_SITE.getPrice());

            goCampingProviderService.updateCampSite(camp, carSiteCnt, CAR_SITE, null,
                    CAR_SITE.getMaximum_people(), CAR_SITE.getPrice());

            goCampingProviderService.updateCampSite(camp, glampSiteCnt, GLAMP_SITE, glampInnerFacility,
                    GLAMP_SITE.getMaximum_people(), GLAMP_SITE.getPrice());

            goCampingProviderService.updateCampSite(camp, caravSiteCnt, CARAV_SITE, caravInnerFacility,
                    CAR_SITE.getMaximum_people(), CAR_SITE.getPrice());

            goCampingProviderService.updateCampSite(camp, personalCaravanSiteCnt, PERSONAL_CARAV_SITE,
                    null, PERSONAL_CARAV_SITE.getMaximum_people(), PERSONAL_CARAV_SITE.getPrice());

        }
        return goCampingParsedResponseDtoList;
    }

    @Transactional
    public int deleteCampByGoCampingData(GoCampingDataDto goCampingDataDto) {
        List<GoCampingDataDto.Item> items = goCampingDataDto.getResponse().getBody().getItems().getItem();
        List<GoCampingParsedResponseDto> goCampingParsedResponseDtoList = goCampingMapper.toGoCampingParsedResponseDtoList(items);

        List<Long> idsToDelete = goCampingParsedResponseDtoList.stream()
                .map(GoCampingParsedResponseDto::getContentId)
                .toList();

        return campRepository.deleteByIds(idsToDelete);
    }
}
