import com.datasiqn.commandcore.argument.ArgumentType;
import com.datasiqn.commandcore.argument.StringArgumentReader;
import com.datasiqn.resultapi.Result;
import org.bukkit.Material;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import static com.datasiqn.commandcore.argument.ArgumentType.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ArgumentParserTest {
    @Test
    public void testWord() {
        testOk("hello there", WORD, "hello");
    }

    @Test
    public void testName() {
        testOk("very cool name", NAME, "very cool name");
    }

    @Test
    public void testInt() {
        testOk("29", INTEGER, 29);
        testOk("-13", INTEGER, -13);
        testErr("bla", INTEGER);
        testErr("12.3", INTEGER);
    }

    @Test
    public void testNaturalNumber() {
        testOk("382", NATURAL_NUMBER, 382);
        testErr("0", NATURAL_NUMBER);
        testErr("12.3", NATURAL_NUMBER);
    }

    @Test
    public void testDouble() {
        testOk("38.2", DOUBLE, 38.2);
        testOk("-1.202", DOUBLE, -1.202);
        testErr("word", DOUBLE);
    }

    @Test
    public void testBoolean() {
        testOk("true", BOOLEAN, true);
        testOk("false", BOOLEAN, false);
        testOk("TrUE", BOOLEAN, true);
        testErr("1", BOOLEAN);
    }

    @Test
    public void testUuid() {
        testOk("9bf53faf-7391-4dee-a47f-e3313af0f243", UUID, java.util.UUID.fromString("9bf53faf-7391-4dee-a47f-e3313af0f243"));
        testErr("382a-dcm-d", UUID);
    }

    @Test
    public void testVector() {
        testOk("12 83 2", VECTOR, new Vector(12, 83, 2));
        testErr("8.2 8 1", VECTOR);
        testErr("bla word a", VECTOR);
    }

    @Test
    public void testMaterial() {
        testOk("stick", MATERIAL, Material.STICK);
        testOk("DIAmOnd_SWOrd", MATERIAL, Material.DIAMOND_SWORD);
        testErr("sick", MATERIAL);
    }

    @Test
    public void testBlock() {
        testOk("stone", BLOCK, Material.STONE);
        testErr("stick", BLOCK);
        testErr("random", BLOCK);
    }

    @Test
    public void testItem() {
        testOk("bedrock", ITEM, Material.BEDROCK);
        testErr("wall_torch", ITEM);
    }

    // Skip player because that won't work in a testing environment
    // public void testPlayer() {}

    // Skip command because that won't work in a testing environment
    // public void testCommand() {}

    @Test
    public void testRangedInt() {
        ArgumentType<Integer> minRanged = rangedInt(8);
        testOk("10", minRanged, 10);
        testOk("8", minRanged, 8);
        testErr("3", minRanged);
        testErr("9.23", minRanged);
        testErr("string", minRanged);

        ArgumentType<Integer> ranged = rangedInt(2, 10);
        testOk("7", ranged, 7);
        testOk("2", ranged, 2);
        testOk("10", ranged, 10);
        testErr("1", ranged);
        testErr("13", ranged);
        testErr("8.9", ranged);
        testErr("number", ranged);
    }

    private <T> void testOk(String arg, @NotNull ArgumentType<T> type, T val) {
        Result<T, String> result = type.parse(new StringArgumentReader(arg));
        assertTrue(result.isOk());
        assertEquals(result.unwrap(), val);
    }

    private void testErr(String arg, @NotNull ArgumentType<?> type) {
        assertTrue(type.parse(new StringArgumentReader(arg)).isError());
    }
}
