package site.campingon.campingon.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // TODO: 참고 예시입니다.
    /*
    400 BAD_REQUEST : 잘못된 요청
    HAS_EMAIL(HttpStatus.BAD_REQUEST, "ACCOUNT-002", "존재하는 이메일입니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "ACCOUNT-003", "비밀번호가 일치하지 않습니다."),

    401 UNAUTHORIZED : 인증되지 않은 사용자
    INVALID_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, "권한 정보가 없는 토큰입니다."),

    404 NOT_FOUND : Resource를 찾을 수 없음
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 정보의 사용자를 찾을 수 없습니다."),

    409 : CONFLICT : Resource의 현재 상태와 충돌. 보통 중복된 데이터 존재
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT, "데이터가 이미 존재합니다."),
    DUPLICATE_CATEGORY(HttpStatus.CONFLICT, "CATEGORY-03", "이미 존재하는 카테고리입니다.");
    */

    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "AUTH-001", "비밀번호가 일치하지 않습니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH-002", "리프레시 토큰이 만료되었습니다."),
    NO_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-003", "토큰이 존재하지 않습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-004", "유효하지 않은 토큰입니다."),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "AUTH-005", "인증되지 않은 유저입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH-006", "유효하지 않은 사용자 이름 또는 비밀번호입니다."),
    INVALID_SECRET_KEY(HttpStatus.UNAUTHORIZED, "AUTH-007", "유효하지 않은 비밀 키입니다."),
    DELETE_USER_DENIED(HttpStatus.FORBIDDEN, "AUTH-008", "회원 탈퇴가 거부되었습니다."),
    ROLE_NOT_FOUND(HttpStatus.FORBIDDEN, "AUTH-009", "권한 정보가 없습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "AUTH-010", "접근 권한이 없습니다."),

    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "ACCOUNT-001", "이미 존재하는 이메일입니다."),
    USER_NOT_FOUND_BY_EMAIL(HttpStatus.NOT_FOUND, "ACCOUNT-002", "해당 이메일의 회원을 찾을 수 없습니다."),
    USER_NOT_FOUND_BY_ID(HttpStatus.NOT_FOUND, "ACCOUNT-003", "해당 아이디의 회원을 찾을 수 없습니다."),
    DUPLICATED_NICKNAME(HttpStatus.CONFLICT, "ACCOUNT-004", "이미 사용 중인 닉네임입니다."),
    PASSWORD_MISMATCH(HttpStatus.BAD_REQUEST, "ACCOUNT-005", "현재 비밀번호가 일치하지 않습니다."),
    PASSWORD_SAME_AS_OLD(HttpStatus.BAD_REQUEST, "ACCOUNT-006", "새로운 비밀번호는 현재 비밀번호와 달라야 합니다."),
    LOGIN_FAILED(HttpStatus.BAD_REQUEST, "ACCOUNT-007", "유저를 찾을 수 없습니다."),

    CAMP_NOT_FOUND_BY_ID(HttpStatus.NOT_FOUND, "CAMP-001", "캠핑장의 ID를 찾을 수 없습니다."),
    CAMP_NOT_FOUND(HttpStatus.NOT_FOUND, "CAMP-002", "캠핑장을 찾을 수 없습니다."),

    REVIEW_NOT_FOUND_BY_ID(HttpStatus.NOT_FOUND, "REVIEW-001", "해당 리뷰를 찾을 수 없습니다."),
    REVIEW_NOT_IN_CAMP(HttpStatus.NOT_FOUND, "REVIEW-002", "리뷰가 해당 캠프에 속하지 않습니다."),

    CAMP_INDUTY_NOT_FOUND(HttpStatus.NOT_FOUND, "INDUTY-001", "존재하지 않는 업종입니다."),

    CAMPSITE_NOT_FOUND_BY_ID(HttpStatus.NOT_FOUND, "CAMPSITE-001", "해당 ID의 캠핑지를 찾을 수 없습니다."),

    BOOKMARK_USER_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKMARK-001", "찜하기를 요청한 사용자를 찾을 수 없습니다."),

    RESERVATION_STATUS_NOT_FOUND(HttpStatus.NOT_FOUND, "RESERVATION-001", "알 수없는 예약 상태입니다."),
    RESERVATION_NOT_FOUND_BY_ID(HttpStatus.NOT_FOUND, "RESERVATION-002", "해당 예약 ID를 찾을 수 없습니다."),
    RESERVATION_NOT_CANCELED(HttpStatus.BAD_REQUEST, "RESERVATION-003", "예약취소를 할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

}
