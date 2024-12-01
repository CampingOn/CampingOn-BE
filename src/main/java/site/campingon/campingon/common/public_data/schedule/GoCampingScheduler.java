package site.campingon.campingon.common.public_data.schedule;

import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.io.ParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import site.campingon.campingon.common.public_data.dto.GoCampingDataDto;
import site.campingon.campingon.common.public_data.dto.GoCampingImageDto;
import site.campingon.campingon.common.public_data.dto.GoCampingImageParsedResponseDto;
import site.campingon.campingon.common.public_data.dto.GoCampingParsedResponseDto;
import site.campingon.campingon.common.public_data.service.GoCampingService;

import java.net.URISyntaxException;
import java.util.List;

@RequiredArgsConstructor
@Component
@Slf4j
public class GoCampingScheduler {
    private final GoCampingService goCampingService;

//    @Scheduled(initialDelay = 10000)    //스케줄러에 등록되고 10초뒤에 메소드 실행 -> 최초에 한번 실행
    @Scheduled(cron = "0 0 1 * * ?", zone = "Asia/Seoul")  //매달 1일 오전 00:00에 실행
    public void scheduleCampCreation() {
        try {
            // 1. numOfRows와 pageNo를 동적으로 조정 가능
            Long numOfRows = 100L; // 가져올 데이터 개수
            Long pageNo = 1L;      // 시작 페이지 번호

            // 2. 공공데이터 API 호출 및 데이터 처리
            GoCampingDataDto goCampingDataDto = goCampingService.getAndConvertToGoCampingDataDto(
                    "numOfRows", numOfRows.toString(),
                    "pageNo", pageNo.toString()
            );

            List<GoCampingParsedResponseDto> goCampingParsedResponseDtos =
                    goCampingService.createCampByGoCampingData(goCampingDataDto);

            // 성공 로그
            log.info("캠프 데이터 생성 성공: " + goCampingParsedResponseDtos.size() + "개");

            // 이미지 데이터 서비스로직 추가

            Long imageCnt = 10L;

            //공공데이터를 조회하고 dto로 변환
            List<GoCampingImageDto> goCampingImageDto = goCampingService.getAndConvertToGoCampingImageDataDto(imageCnt);

            //CampImage 를 생성하고 DB에 저장한다.
            List<List<GoCampingImageParsedResponseDto>> goCampingImageParsedResponseDtos
                    = goCampingService.createCampImageByGoCampingImageData(goCampingImageDto);

            log.info("캠프 이미지 데이터 생성 성공: " + goCampingImageParsedResponseDtos.size() + "개");

        } catch (URISyntaxException e) {
            // 예외 처리
            log.error("캠프 데이터 생성 실패: " + e.getMessage());
        } catch (ParseException e) {
            log.error("캠프 데이터 생성 실패: " + e.getMessage());
        }
    }
}
