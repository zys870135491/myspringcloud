import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class test {

    public static void main(String[] args) {
        /*ZonedDateTime now = ZonedDateTime.now();
        System.out.println(now);*/


        LocalDateTime endTime = LocalDateTime.of(2020, 5, 20, 5, 20, 10,00);

        System.out.println(endTime);
    }

}
