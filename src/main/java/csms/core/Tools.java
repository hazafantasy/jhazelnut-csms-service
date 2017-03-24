package csms.core;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Tools {

    public static LocalDateTime date2LocalDateTime(Date date) {
        return LocalDateTime.ofInstant(
                date.toInstant(), ZoneId.systemDefault());
    }

}
