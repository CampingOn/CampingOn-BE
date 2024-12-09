package site.campingon.campingon.reservation.entity;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public enum CheckTime {
    CHECKIN(LocalTime.of(15, 0)),
    CHECKOUT(LocalTime.of(11, 0));

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private final LocalTime time;

    @JsonValue
    public String getFormattedTime() {
        return time.format(FORMATTER);
    }

}