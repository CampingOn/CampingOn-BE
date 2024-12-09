package site.campingon.campingon.common.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import site.campingon.campingon.common.exception.ErrorCode;
import site.campingon.campingon.common.exception.GlobalException;
import site.campingon.campingon.reservation.entity.CheckTime;

import java.sql.Time;
import java.util.Arrays;

@Converter(autoApply = true)
public class CheckTimeConverter implements AttributeConverter<CheckTime, Time> {

    // DB에 저장할 때 (LocalTime -> Time)
    @Override
    public Time convertToDatabaseColumn(CheckTime checkTime) {

        return Time.valueOf(checkTime.getTime());
    }

    // DB에 꺼내서 조회할 때 (Time -> LocalTime)
    @Override
    public CheckTime convertToEntityAttribute(Time dbData) {

        return Arrays.stream(CheckTime.values())
                .filter(checkTime -> checkTime.getTime().equals(dbData.toLocalTime()))
                .findFirst()
                .orElseThrow(() -> new GlobalException(ErrorCode.RESERVATION_INVALID_CHECKTIME));

    }
}