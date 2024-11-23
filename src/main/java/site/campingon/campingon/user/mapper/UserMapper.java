package site.campingon.campingon.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import site.campingon.campingon.user.dto.*;
import site.campingon.campingon.user.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // 회원가입 요청 DTO -> User Entity 변환
    @Mapping(target = "id", ignore = true) // DB에서 자동 생성
    @Mapping(target = "role", constant = "USER") // 기본 Role 설정
    @Mapping(target = "isDeleted", constant = "false") // 기본값
    User toEntity(UserSignUpRequestDto userSignUpRequestDto);

    // User Entity -> 회원가입 응답 DTO 변환
    UserSignUpResponseDto toSignUpResponseDto(User user);

    // User Entity -> 회원 정보 조회 DTO 변환
    UserResponseDto toResponseDto(User user);

}