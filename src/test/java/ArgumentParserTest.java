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

import java.util.function.Predicate;

import static com.datasiqn.commandcore.argument.type.ArgumentType.*;
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
    public void testFloat() {
        testOk("2.3", FLOAT, 2.3f);
        testOk("-283.287", FLOAT, -283.287f);
        testErr("number", FLOAT);
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

    @Test
    public void testRangedDouble() {
        ArgumentType<Double> minRanged = rangedDouble(2.3);
        testOk("8.2", minRanged, 8.2);
        testOk("2.5", minRanged, 2.5);
        testOk("2.3", minRanged, 2.3);
        testErr("2.2", minRanged);
        testErr("-8.2", minRanged);
        testErr("blaalakd", minRanged);

        ArgumentType<Double> ranged = rangedDouble(-1.1, 8.2);
        testOk("7", ranged, 7.0);
        testOk("2.2", ranged, 2.2);
        testOk("-1.1", ranged, -1.1);
        testOk("8.2", ranged, 8.2);
        testErr("8.3", ranged);
        testErr("13", ranged);
        testErr("-2.8", ranged);
        testErr("aaaaaa", ranged);
    }

    @Test
    public void testRangedFloat() {
        ArgumentType<Float> minRanged = rangedFloat(2.3f);
        testOk("8.2", minRanged, 8.2f);
        testOk("2.5", minRanged, 2.5f);
        testOk("2.3", minRanged, 2.3f);
        testErr("2.2", minRanged);
        testErr("-8.2", minRanged);
        testErr("blaalakd", minRanged);

        ArgumentType<Float> ranged = rangedFloat(-1.1f, 8.2f);
        testOk("7", ranged, 7.0f);
        testOk("2.2", ranged, 2.2f);
        testOk("-1.1", ranged, -1.1f);
        testOk("8.2", ranged, 8.2f);
        testErr("8.3", ranged);
        testErr("13", ranged);
        testErr("-2.8", ranged);
        testErr("aaaaaa", ranged);
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
