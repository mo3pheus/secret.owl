package bootstrap;

import org.joda.time.DateTimeZone;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class DriverTest {

    @Test
    public void testDriverMock() {
        System.out.println("Hello this is mock test");
        Set<String> availableTimezones = DateTimeZone.getAvailableIDs();

        for (String timezone : availableTimezones) {
            System.out.println(timezone);
        }
//        System.out.println("Deliberately failing!");
//        assertTrue(false);
    }
}