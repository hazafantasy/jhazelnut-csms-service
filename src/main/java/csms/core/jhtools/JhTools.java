package csms.core.jhtools;

import com.google.api.client.util.DateTime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JhTools {

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
