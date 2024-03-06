import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.argument.StringArgumentReader;
import org.junit.Test;

import static org.junit.Assert.*;

public class ArgumentReaderTest {
    @Test
    public void testNextGet() {
        ArgumentReader reader = new StringArgumentReader("cool");
        assertEquals('c', reader.get());
        assertEquals('o', reader.next());
        reader.next();
        assertFalse(reader.atEnd());
        reader.next(); // reader at 'l'
        assertEquals('l', reader.get());
    }

    @Test
    public void testNextWord() {
        ArgumentReader reader = new StringArgumentReader("i love bugs");
        assertEquals("i", reader.nextWord());
        reader.next();
        assertEquals("love", reader.nextWord());
        reader.next();
        assertEquals("bugs", reader.nextWord());
        assertTrue(reader.atEnd());
    }

    @Test
    public void testAtEnd() {
        ArgumentReader reader = new StringArgumentReader("that cool");
        reader.nextWord();
        reader.next();
        reader.nextWord();
        assertTrue(reader.atEnd());
    }

    @Test
    public void testRest() {
        ArgumentReader reader = new StringArgumentReader("very much super cool");
        reader.nextWord();
        reader.next();
        assertEquals(reader.rest(), "much super cool");
        assertTrue(reader.atEnd());
    }
}
