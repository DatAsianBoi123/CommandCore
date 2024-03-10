import com.datasiqn.commandcore.argument.numrange.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class NumberRangeTest {
    @Test
    public void testFrom() {
        NumberRange<Integer> range = new FromNumberRange<>(3);
        assertTrue(range.contains(5));
        assertTrue(range.contains(3));
        assertTrue(range.contains(9));
        assertFalse(range.contains(2));
        assertFalse(range.contains(-2));
    }

    @Test
    public void testTo() {
        NumberRange<Double> range = new ToNumberRange<>(10.0);
        assertTrue(range.contains(3.82));
        assertTrue(range.contains(10.0));
        assertTrue(range.contains(8.28));
        assertFalse(range.contains(10.01));
        assertFalse(range.contains(188.2));
    }

    @Test
    public void testFromTo() {
        NumberRange<Double> range = new FromToNumberRange<>(2.0, 5.0);
        assertTrue(range.contains(3.2));
        assertTrue(range.contains(2.0));
        assertTrue(range.contains(5.0));
        assertFalse(range.contains(1.2));
        assertFalse(range.contains(8.2));
    }

    @Test
    public void testSingle() {
        NumberRange<Integer> range = new SingleNumberRange<>(3);
        assertTrue(range.contains(3));
        assertFalse(range.contains(2));
        assertFalse(range.contains(8));
        assertFalse(range.contains(0));
    }

    @Test
    public void testFull() {
        NumberRange<Double> range = new FullNumberRange<>();
        assertTrue(range.contains(-28.0));
        assertTrue(range.contains(1000.0));
        assertTrue(range.contains(Double.NEGATIVE_INFINITY));
        assertTrue(range.contains(0.0));
        assertTrue(range.contains(-203.233));
    }
}
