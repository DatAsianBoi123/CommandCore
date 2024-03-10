import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.commandcore.argument.ArgumentReader.ReadUntilResult;
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
    public void testReadUntil() {
        ArgumentReader reader = new StringArgumentReader("[this is,some list of,things]");
        reader.next();
        assertEquals("this is", reader.readUntil(',', ']'));
        reader.next();
        assertEquals("some list of", reader.readUntil(',', ']'));
        reader.next();
        assertEquals("things", reader.readUntil(',', ']'));
        assertEquals(reader.get(), ']');
    }

    @Test
    public void testReadUntilEscaped() {
        ArgumentReader reader = new StringArgumentReader("[thing,and\\, thing,[ooh thing in brackets\\],something \\ other thing,ending \\\\]");
        reader.next();
        assertEquals(reader.readUntilEscaped(',', ']'), ReadUntilResult.found("thing"));
        reader.next();
        assertEquals(reader.readUntilEscaped(',', ']'), ReadUntilResult.found("and, thing"));
        reader.next();
        assertEquals(reader.readUntilEscaped(',', ']'), ReadUntilResult.found("[ooh thing in brackets]"));
        reader.next();
        assertEquals(reader.readUntilEscaped(',', ']'), ReadUntilResult.found("something \\ other thing"));
        reader.next();
        assertEquals(reader.readUntilEscaped(',', ']'), ReadUntilResult.found("ending \\"));
        assertEquals(reader.get(), ']');

        reader = new StringArgumentReader("something,not found\\,");
        assertEquals(reader.readUntilEscaped(','), ReadUntilResult.found("something"));
        reader.next();
        assertEquals(reader.readUntilEscaped(','), ReadUntilResult.notFound("not found,"));
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
