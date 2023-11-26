import com.datasiqn.commandcore.argument.StringArgumentReader;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.resultapi.Result;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static com.datasiqn.commandcore.argument.type.ArgumentType.*;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class ArgumentParserTest {
    static {
        Bukkit.setServer(new MockServer.Builder()
                .addPlayer("bob")
                .addPlayer("jim")
                .addWorld("world")
                .addWorld("nether")
                .build());
    }

    @Test
    public void testWord() {
        testOk("hello there", WORD, "hello");
    }

    @Test
    public void testName() {
        testOk("very cool name", NAME, "very cool name");
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
    public void testWorld() {
        this.<World>testOk("world", WORLD, world -> world.getName().equals("world"));
        this.<World>testOk("nETHer", WORLD, world -> world.getName().equals("nether"));
        testErr("end", WORLD);
    }

    @Test
    public void testEntity() {
        testOk("zombie", ENTITY, EntityType.ZOMBIE);
        testOk("armor_stand", ENTITY, EntityType.ARMOR_STAND);
        testErr("herobrine", ENTITY);
    }

    @Test
    public void testLivingEntity() {
        testOk("player", LIVING_ENTITY, EntityType.PLAYER);
        testOk("enderman", LIVING_ENTITY, EntityType.ENDERMAN);
        testErr("egg", LIVING_ENTITY);
    }

    @Test
    public void testSpawnableEntity() {
        testOk("dropped_item", SPAWNABLE_ENTITY, EntityType.DROPPED_ITEM);
        testErr("player", SPAWNABLE_ENTITY);
    }

    @Test
    public void testLootTable() {
        testOk("jungle_temple", LOOT_TABLE);
        testOk("buried_treasure", LOOT_TABLE);
        testErr("blabla", LOOT_TABLE);
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

    @Test
    public void testEnum() {
        ArgumentType<UppercaseEnum> uppercaseEnum = new EnumArgumentType<>(UppercaseEnum.class);
        testOk("cOoL_ConstANT", uppercaseEnum, UppercaseEnum.COOL_CONSTANT);
        testOk("thREE", uppercaseEnum, UppercaseEnum.THREE);
        testOk("wow", uppercaseEnum, UppercaseEnum.WOW);
        testErr("not an enum", uppercaseEnum);

        ArgumentType<BadEnum> badEnum = new EnumArgumentType<>(BadEnum.class);
        testOk("pascalcase", badEnum, BadEnum.PascalCase);
        testOk("LOWERCASE", badEnum, BadEnum.lowercase);
        testOk("CameLcAse", badEnum, BadEnum.camelCase);
        testErr("aaaaaa", badEnum);
    }

    @Test
    public void testPlayer() {
        this.<Player>testOk("jim", PLAYER, player -> player.getName().equals("jim"));
        this.<Player>testOk("bob", PLAYER, player -> player.getName().equals("bob"));
        testErr("joe", PLAYER);
        testErr("b", PLAYER);
    }

    // Skip command because that won't work in a testing environment
    // public void testCommand() {}

    @Test
    public void testByte() {
        {
            ArgumentType<Byte> byteType = number(byte.class);
            testOk("29", byteType, (byte) 29);
            testOk("-13", byteType, (byte) -13);
            testErr("280", byteType);
            testErr("bla", byteType);
            testErr("12.3", byteType);
        }

        {
            ArgumentType<Byte> byteType = number(Byte.class);
            testOk("29", byteType, (byte) 29);
            testOk("-13", byteType, (byte) -13);
            testErr("280", byteType);
            testErr("bla", byteType);
            testErr("12.3", byteType);
        }
    }

    @Test
    public void testShort() {
        ArgumentType<Short> shortType = number(short.class);
        testOk("0", shortType, (short) 0);
        testOk("10", shortType, (short) 10);
        testOk("-8", shortType, (short) -8);
        testErr("40000", shortType);
        testErr("bignumber", shortType);
        testErr("8.2", shortType);
    }

    @Test
    public void testInteger() {
        ArgumentType<Integer> intType = number(int.class);
        testOk("2", intType, 2);
        testOk("-3", intType, -3);
        testOk("100", intType, 100);
        testErr("10000000000000", intType);
        testErr("word", intType);
        testErr("-2.3", intType);
    }

    @Test
    public void testLong() {
        ArgumentType<Long> longType = number(long.class);
        testOk("88", longType, 88L);
        testOk("-10000", longType, -10_000L);
        testErr("10000000000000000000", longType);
        testErr("abbb", longType);
        testErr("8.2", longType);
    }

    @Test
    public void testFloat() {
        ArgumentType<Float> floatType = number(float.class);
        testOk("1", floatType, 1f);
        testOk("8.2", floatType, 8.2f);
        testOk("-188.333", floatType, -188.333f);
        testErr("25performances??", floatType);
    }

    @Test
    public void testDouble() {
        ArgumentType<Double> doubleType = number(double.class);
        testOk("8.2", doubleType, 8.2);
        testOk("11.2", doubleType, 11.2);
        testErr("tax,license,andfees", doubleType);
    }

    @Test
    public void testNumber() {
        assertThrows(IllegalArgumentException.class, () -> number(AtomicInteger.class));
    }

    @Test
    public void testRangedNumber() {
        ArgumentType<Double> rangedMin = rangedNumber(double.class, 5.2);
        testOk("5.2", rangedMin, 5.2);
        testOk("58.1", rangedMin, 58.1);
        testErr("4.9", rangedMin);
        testErr("-2.1", rangedMin);
        testErr("modernfamily", rangedMin);

        ArgumentType<Double> ranged = rangedNumber(double.class, 0.1, 5.6);
        testOk("5", ranged, 5.0);
        testOk("0.1", ranged, 0.1);
        testOk("5.6", ranged, 5.6);
        testErr("0", ranged);
        testErr("-10", ranged);
        testErr("28.3", ranged);
        testErr("al", ranged);
    }

    private <T> void testOk(String arg, @NotNull ArgumentType<T> type) {
        this.<T>testOk(arg, type, val -> true);
    }
    private <T> void testOk(String arg, @NotNull ArgumentType<T> type, T val) {
        this.<T>testOk(arg, type, parsed -> parsed.equals(val));
    }
    private <T> void testOk(String arg, @NotNull ArgumentType<T> type, @NotNull Predicate<T> tester) {
        Result<T, String> result = type.parse(new StringArgumentReader(arg));
        assertTrue(result.isOk());
        assertTrue(tester.test(result.unwrap()));
    }

    private void testErr(String arg, @NotNull ArgumentType<?> type) {
        assertTrue(type.parse(new StringArgumentReader(arg)).isError());
    }

    private enum UppercaseEnum {
        COOL_CONSTANT,
        THREE,
        WOW,
    }

    private enum BadEnum {
        PascalCase,
        lowercase,
        camelCase,
    }
}
