package site.campingon.campingon.common.public_data.schedule;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.campingon.campingon.common.exception.ErrorCode;
import site.campingon.campingon.common.exception.GlobalException;
import site.campingon.campingon.common.public_data.GoCampingPath;
import site.campingon.campingon.common.public_data.dto.GoCampingDataDto;
import site.campingon.campingon.common.public_data.dto.GoCampingImageDto;
import site.campingon.campingon.common.public_data.dto.GoCampingImageParsedResponseDto;
import site.campingon.campingon.common.public_data.dto.GoCampingParsedResponseDto;
import site.campingon.campingon.common.public_data.service.GoCampingService;

import java.net.URISyntaxException;
import java.util.List;

import static site.campingon.campingon.common.public_data.PublicDataConstants.*;

/**
 * 고캠핑 데이터 수집 스케줄러
 * 매달 1,2,3일 00:00에 데이터 수집
 * 1일 : 신규 데이터
 * 2일 : 데이터 업데이트
 * 3일 : 데이터 삭제
 * 스케줄러 테스트해보고싶다면 다음 어노테이션 사용하기
 *
 * @Scheduled(initialDelay = 10000)    //스케줄러에 등록되고 10초뒤에 메소드 실행 -> 최초에 한번 실행
 */

@RequiredArgsConstructor
@Component
@Slf4j
public class GoCampingScheduler {
    private final GoCampingService goCampingService;

    private static final long NUM_OF_ROWS = 500L;   //while 문이 한번돌때 저장되는 개수
    private static final long IMAGE_CNT = 10L;  //Camp id 하나에 저장될 이미지 개수

    //신규 데이터 저장
    @Scheduled(cron = "0 0 1 * * ?", zone = "Asia/Seoul")  //매달 1일 오전 00:00에 실행
    public void scheduleCampCreation() {
        try {
            long pageNo = 1L;      // 현재 페이지 번호
            long totalCount = 0L;   //전체 데이터 개수

            log.info("캠프 생성 스케줄러 실행");
            while (true) {
                //고캠핑 API 호출하고 dto 로 변환(이미지)
                GoCampingDataDto goCampingDataDto = goCampingService.getAndConvertToGoCampingDataDto(
                        GoCampingPath.BASED_SYNC_LIST,
                        "numOfRows", Long.toString(NUM_OF_ROWS),
                        "pageNo", Long.toString(pageNo),
                        "syncStatus", SYNC_STATUS_NEW
                );

                if (goCampingDataDto == null) {
                    throw new GlobalException(ErrorCode.GO_CAMPING_DATA_NO_CONTENT);
                }

                //공공 API 가져올 총 cnt 추출
                totalCount = goCampingDataDto.getResponse().getBody().getTotalCount();

                List<GoCampingParsedResponseDto> goCampingParsedResponseDtos =
                        goCampingService.createOrUpdateCampByGoCampingData(goCampingDataDto);

                log.info("캠프 데이터 신규 생성 성공: " + goCampingParsedResponseDtos.size() + "개");

                //캠프 아이디 추출
                List<Long> campIdList = goCampingParsedResponseDtos.stream()
                        .map(GoCampingParsedResponseDto::getContentId)
                        .toList();

                //고캠핑 API 호출하고 dto 로 변환(이미지)
                List<GoCampingImageDto> goCampingImageDto
                        = goCampingService.getAndConvertToGoCampingImageDataDto(campIdList, IMAGE_CNT);

                //CampImage 를 생성하고 DB에 저장한다.
                List<List<GoCampingImageParsedResponseDto>> goCampingImageParsedResponseDtos
                        = goCampingService.createOrUpdateCampImageByGoCampingImageData(goCampingImageDto);

                log.info("캠프 이미지 데이터 신규 생성 성공: " + goCampingImageParsedResponseDtos.size() + "개");

                //더이상 가져올 데이터(Row)가 없거나 페이지번호 * 현재페이지수가 totalCnt 보다 크면 break
                if (goCampingDataDto.getResponse().getBody().getNumOfRows() == 0 ||
                        NUM_OF_ROWS * pageNo >= totalCount) {
                    break;
                }

                pageNo++;   //페이지 번호 증가
            }
        } catch (URISyntaxException e) {
            log.error("캠프 데이터 생성 실패: " + e.getMessage());
        }
    }

    //데이터 업데이트
    @Scheduled(cron = "0 0 0 2 * ?", zone = "Asia/Seoul")   //매월 2일 00:00 실행
    public void scheduleCampUpdate() {
        long pageNo = 1L;      // 현재 페이지 번호
        long totalCount = 0L;   //전체 데이터 개수

        log.info("캠프 업데이트 스케줄러 실행");

        try {
            while (true) {
                GoCampingDataDto goCampingDataDto = goCampingService.getAndConvertToGoCampingDataDto(
                        GoCampingPath.BASED_SYNC_LIST,
                        "numOfRows", Long.toString(NUM_OF_ROWS),
                        "pageNo", Long.toString(pageNo),
                        "syncStatus", SYNC_STATUS_UPDATE
                );

                if (goCampingDataDto == null) {
                    throw new GlobalException(ErrorCode.GO_CAMPING_DATA_NO_CONTENT);
                }

                totalCount = goCampingDataDto.getResponse().getBody().getTotalCount();

                List<GoCampingParsedResponseDto> goCampingParsedResponseDtos =
                        goCampingService.createOrUpdateCampByGoCampingData(goCampingDataDto);

                List<Long> campIdList = goCampingParsedResponseDtos.stream()
                        .map(GoCampingParsedResponseDto::getContentId)
                        .toList();

                log.info("캠프 데이터 업데이트 성공: " + goCampingParsedResponseDtos.size() + "개");

                List<GoCampingImageDto> goCampingImageDto
                        = goCampingService.getAndConvertToGoCampingImageDataDto(campIdList, IMAGE_CNT);

                List<List<GoCampingImageParsedResponseDto>> goCampingImageParsedResponseDtos
                        = goCampingService.createOrUpdateCampImageByGoCampingImageData(goCampingImageDto);

                log.info("캠프 이미지 데이터 업데이트 성공: " + goCampingImageParsedResponseDtos.size() + "개");

                if (goCampingDataDto.getResponse().getBody().getNumOfRows() == 0 ||
                        NUM_OF_ROWS * pageNo >= totalCount) {
                    break;
                }

                pageNo++;   //페이지 번호 증가
            }
        } catch (URISyntaxException e) {
            log.error("캠프 데이터 업데이트 실패: " + e.getMessage());
        }

    }

    //불필요한 데이터 삭제
    @Scheduled(cron = "0 0 0 3 * ?", zone = "Asia/Seoul")   //매월 3일 00:00 실행
    public void scheduleCampDelete() {
        long pageNo = 1L;      // 현재 페이지 번호
        long totalCount = 0L;   //전체 데이터 개수

        log.info("캠프 삭제 스케줄러 실행");
        try {
            while (true) {
                GoCampingDataDto goCampingDataDto = goCampingService.getAndConvertToGoCampingDataDto(
                        GoCampingPath.BASED_SYNC_LIST,
                        "numOfRows", Long.toString(NUM_OF_ROWS),
                        "pageNo", Long.toString(pageNo),
                        "syncStatus", SYNC_STATUS_DELETE
                );

                if (goCampingDataDto == null) {
                    throw new GlobalException(ErrorCode.GO_CAMPING_DATA_NO_CONTENT);
                }

                totalCount = goCampingDataDto.getResponse().getBody().getTotalCount();
                int deleteCnt = goCampingService.deleteCampByGoCampingData(goCampingDataDto);

                log.info("캠프 데이터 삭제 성공: " + deleteCnt + "개");

                pageNo++;   //페이지 번호 증가

                if (goCampingDataDto.getResponse().getBody().getNumOfRows() == 0 ||
                        NUM_OF_ROWS * pageNo >= totalCount) {
                    break;
                }

                pageNo++;   //페이지 번호 증가
            }
        } catch (URISyntaxException e) {
            log.error("캠프 데이터 삭제 실패: " + e.getMessage());
        }
    }
}
