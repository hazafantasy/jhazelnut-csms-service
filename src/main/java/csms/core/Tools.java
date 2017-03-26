package csms.core;

import com.google.api.client.util.DateTime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Tools {

    public static LocalDateTime date2LocalDateTime(Date date) {
        return LocalDateTime.ofInstant(
                date.toInstant(), ZoneId.systemDefault());
    }

    public static LocalDateTime dateTime2LocalDateTime(DateTime dateTime) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(dateTime.getValue()),
                ZoneId.systemDefault());
    }

}
