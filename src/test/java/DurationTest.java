import com.datasiqn.commandcore.argument.duration.Duration;
import com.datasiqn.commandcore.argument.duration.TimeUnit;
import org.junit.Test;

import static org.junit.Assert.*;

public class DurationTest {
    @Test
    public void testFrom() {
        assertEquals(50, Duration.from(50, TimeUnit.TICKS).ticks());
        // 0 because ticks are stored as a long, meaning conversion from ms to ticks will get truncated
        assertEquals(0, Duration.from(12, TimeUnit.MILLIS).ticks(), 0.0001);
        assertEquals(27, Duration.from(1382, TimeUnit.MILLIS).ticks(), 0.0001);
        assertEquals(100, Duration.from(5, TimeUnit.SECONDS).ticks());
        assertEquals(3600, Duration.from(3, TimeUnit.MINUTES).ticks());
        assertEquals(144000, Duration.from(2, TimeUnit.HOURS).ticks());
    }

    @Test
    public void testAs() {
        Duration duration = Duration.from(3800, TimeUnit.TICKS);

        assertEquals(3800, duration.as(TimeUnit.TICKS), 0.0001);
        assertEquals(3800, duration.ticks());
        assertEquals(190000, duration.as(TimeUnit.MILLIS), 0.0001);
        assertEquals(190, duration.as(TimeUnit.SECONDS), 0.0001);
        assertEquals(3.1666667, duration.as(TimeUnit.MINUTES), 0.0001);
        assertEquals(0.0527778, duration.as(TimeUnit.HOURS), 0.0001);
    }
}
