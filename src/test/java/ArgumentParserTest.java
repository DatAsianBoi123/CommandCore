import com.datasiqn.commandcore.argument.StringArgumentReader;
import com.datasiqn.commandcore.argument.duration.Duration;
import com.datasiqn.commandcore.argument.duration.TimeUnit;
import com.datasiqn.commandcore.argument.numrange.*;
import com.datasiqn.commandcore.argument.selector.EntitySelector;
import com.datasiqn.commandcore.argument.selector.SelectorRequirements;
import com.datasiqn.commandcore.argument.selector.SingleEntitySelector;
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.argument.type.EnumArgumentType;
import com.datasiqn.resultapi.Result;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

import static com.datasiqn.commandcore.argument.type.ArgumentType.*;
import static org.junit.Assert.*;

@SuppressWarnings("deprecation")
public class ArgumentParserTest {
    private static final java.util.UUID BOB_UUID = java.util.UUID.randomUUID();
    private static final java.util.UUID JIM_UUID = java.util.UUID.randomUUID();

    private static final java.util.UUID PIG_UUID = java.util.UUID.randomUUID();
    private static final java.util.UUID ITEM_FRAME_UUID = java.util.UUID.randomUUID();

    static {
        Bukkit.setServer(new MockServer.Builder()
                .addPlayer("bob", BOB_UUID)
                .addPlayer("jim", JIM_UUID)
                .addEntity(EntityType.PIG, PIG_UUID)
                .addEntity(EntityType.ITEM_FRAME, ITEM_FRAME_UUID)
                .addWorld("world")
                .addWorld("nether")
                .addRecipe(new MockRecipe(new NamespacedKey("coolplugin", "coolrecipe"), new ItemStack(Material.BLACK_DYE)))
                .addRecipe(new MockRecipe(NamespacedKey.minecraft("diamond_sword"), new ItemStack(Material.DIAMOND_SWORD)))
                .build());
    }

    @Test
    public void testWord() {
        testOk("hello there", WORD, "hello");
    }

    @Test
    public void testQuotedWord() {
        testOk("\"\"", QUOTED_WORD, "");
        testOk("\"cool name\" bob", QUOTED_WORD, "cool name");
        testOk("\"hi\"", QUOTED_WORD, "hi");
        testOk("\"name with \\\"quotes\\\"\"", QUOTED_WORD, "name with \"quotes\"");
        testOk("\"word with backslashes \\\\\"", QUOTED_WORD, "word with backslashes \\");
        testOk("\"another word with \\ backslashes\"", QUOTED_WORD, "another word with \\ backslashes");
        testErr("\"", QUOTED_WORD);
        testErr("name \"", QUOTED_WORD);
        testErr("\" aaaa another arg", QUOTED_WORD);
        testErr("\"some thing\"uh oh", QUOTED_WORD);
        testErr("\"woah is that \\\"jim\\\"?", QUOTED_WORD);
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
    public void testDuration() {
        testOk("1800t", DURATION, Duration.from(1800, TimeUnit.TICKS));
        testOk("382ms", DURATION, Duration.from(382, TimeUnit.MILLIS));
        testOk("38s", DURATION, Duration.from(38, TimeUnit.SECONDS));
        testOk("5min", DURATION, Duration.from(5, TimeUnit.MINUTES));
        testOk("1.38h", DURATION, Duration.from(1.38, TimeUnit.HOURS));
        testErr("", DURATION);
        testErr("283", DURATION);
        testErr("3.2.3.4.2t", DURATION);
        testErr("800a", DURATION);
        testErr("43dfaas", DURATION);
        testErr("-30t", DURATION);
        testErr("ms", DURATION);
    }

    @Test
    public void testUuid() {
        testOk("9bf53faf-7391-4dee-a47f-e3313af0f243", UUID, java.util.UUID.fromString("9bf53faf-7391-4dee-a47f-e3313af0f243"));
        testErr("382a-dcm-d", UUID);
    }

    @Test
    public void testNamespacedKey() {
        testOk("stone", NAMESPACED_KEY, NamespacedKey.minecraft("stone"));
        testOk("plugin:key", NAMESPACED_KEY, new NamespacedKey("plugin", "key"));
        testErr("invalidkey:::", NAMESPACED_KEY);
        testErr("::key", NAMESPACED_KEY);
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
        testOk("zombie", ENTITY_TYPE, EntityType.ZOMBIE);
        testOk("armor_stand", ENTITY_TYPE, EntityType.ARMOR_STAND);
        testErr("herobrine", ENTITY_TYPE);
    }

    @Test
    public void testLivingEntity() {
        testOk("player", LIVING_ENTITY_TYPE, EntityType.PLAYER);
        testOk("enderman", LIVING_ENTITY_TYPE, EntityType.ENDERMAN);
        testErr("egg", LIVING_ENTITY_TYPE);
    }

    @Test
    public void testSpawnableEntity() {
        testOk("dropped_item", SPAWNABLE_ENTITY_TYPE, EntityType.DROPPED_ITEM);
        testErr("player", SPAWNABLE_ENTITY_TYPE);
    }

    @Test
    public void testLootTable() {
        testOk("jungle_temple", LOOT_TABLE);
        testOk("buried_treasure", LOOT_TABLE);
        testErr("blabla", LOOT_TABLE);
    }

    @Test
    public void testRecipe() {
        testOk("coolplugin:coolrecipe", RECIPE);
        testOk("minecraft:diamond_sword", RECIPE);
        testErr(":::aa:", RECIPE);
        testErr("coolrecipe", RECIPE);
        testErr("coolplugin:diamond_sword", RECIPE);
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
        this.<Player>testOk("jim", ONLINE_PLAYER, player -> player.getName().equals("jim"));
        this.<Player>testOk("bob", ONLINE_PLAYER, player -> player.getName().equals("bob"));
        testErr("joe", ONLINE_PLAYER);
        testErr("b", ONLINE_PLAYER);
    }

    // Skip command because that won't work in a testing environment
    // public void testCommand() {}

    @Test
    public void testJson() {
        {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("hi", 12);

            JsonObject inner = new JsonObject();
            JsonArray innerArray = new JsonArray(3);
            innerArray.add(1);
            innerArray.add(3);
            innerArray.add(8);
            inner.add("names", innerArray);

            jsonObject.add("bye", inner);

            testOk("""
                    {"hi": 12, "bye": {"names": [1, 3, 8]}}""", json(JsonObject.class), jsonObject);
        }

        {
            JsonArray jsonArray = new JsonArray();
            jsonArray.add("a");
            jsonArray.add("b");

            JsonObject inner = new JsonObject();
            inner.addProperty("hi", 12);
            inner.addProperty("aaa", "jim");

            jsonArray.add(inner);

            testOk("""
                ["a", "b", {"hi": 12, "aaa": "jim"}]""", json(JsonArray.class), jsonArray);
        }

        testErr("""
                {"hi": true}""", json(JsonArray.class));

        assertThrows(IllegalArgumentException.class, () -> json(Person.class, new TypeToken<School>() {}.getType()));

        Person person = new Person("Jim", 14, true, new School("Cool High School", SchoolType.HIGH), Collections.emptyList());
        testOk("""
                {
                    "name": "Jim",
                    "age": 14,
                    "cool": true,
                    "school": {
                        "name": "Cool High School",
                        "type": "HIGH"
                    },
                    "friends": []
                }""", json(Person.class), person);
    }

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
        // tests if the argument types are being cached
        assertSame(number(int.class), number(int.class));
    }

    @Test
    public void testBoundedNumber() {
        ArgumentType<Double> rangedMin = boundedNumber(double.class, 5.2);
        testOk("5.2", rangedMin, 5.2);
        testOk("58.1", rangedMin, 58.1);
        testErr("4.9", rangedMin);
        testErr("-2.1", rangedMin);
        testErr("modernfamily", rangedMin);

        ArgumentType<Double> ranged = boundedNumber(double.class, 0.1, 5.6);
        testOk("5", ranged, 5.0);
        testOk("0.1", ranged, 0.1);
        testOk("5.6", ranged, 5.6);
        testErr("0", ranged);
        testErr("-10", ranged);
        testErr("28.3", ranged);
        testErr("al", ranged);
    }

    @Test
    public void testNumberRange() {
        ArgumentType<NumberRange<Double>> doubleRange = numberRange(double.class);
        testOk("3", doubleRange, new SingleNumberRange<>(3.0));
        testOk("3.2", doubleRange, new SingleNumberRange<>(3.2));

        testOk("..5", doubleRange, new ToNumberRange<>(5.0));
        testOk("...3", doubleRange, new ToNumberRange<>(0.3));
        testOk("..2.3", doubleRange, new ToNumberRange<>(2.3));

        testOk("3..", doubleRange, new FromNumberRange<>(3.0));
        testOk("3.2..", doubleRange, new FromNumberRange<>(3.2));
        testOk("3..", doubleRange, new FromNumberRange<>(3.0));
        testOk(".2..", doubleRange, new FromNumberRange<>(0.2));

        testOk("0.2..3.2", doubleRange, new FromToNumberRange<>(0.2, 3.2));
        testOk("0...3", doubleRange, new FromToNumberRange<>(0.0, 0.3));
        testOk("10..15", doubleRange, new FromToNumberRange<>(10.0, 15.0));
        testOk(".8..32.3", doubleRange, new FromToNumberRange<>(0.8, 32.3));
        testOk("..", doubleRange, new FullNumberRange<>());

        testErr("3...", doubleRange);
        testErr("...", doubleRange);
        testErr("1.2.2", doubleRange);
        testErr(".", doubleRange);
        testErr("..3..", doubleRange);
    }

    @Test
    public void testSingleEntitySelector() {
        testOk("jim", ENTITIES, (Predicate<EntitySelector<Entity>>) selector -> selector instanceof SingleEntitySelector<Entity>);
        testOk("jim", ENTITIES, (Predicate<EntitySelector<Entity>>) selector -> selector.getFirst(null).getUniqueId().equals(JIM_UUID));
        testOk(JIM_UUID.toString(), ENTITIES, (Predicate<EntitySelector<Entity>>) selector -> selector.getFirst(null).getUniqueId().equals(JIM_UUID));

        testOk("bob", ENTITY, (Predicate<EntitySelector<Entity>>) selector -> selector instanceof SingleEntitySelector<Entity>);
        testOk("bob", ENTITY, (Predicate<EntitySelector<Entity>>) selector -> selector.getFirst(null).getUniqueId().equals(BOB_UUID));
        testOk(BOB_UUID.toString(), PLAYER, (Predicate<EntitySelector<Player>>) selector -> selector.getFirst(null).getUniqueId().equals(BOB_UUID));

        testErr("person", ENTITIES);
        testErr(java.util.UUID.randomUUID().toString(), ENTITIES);
        testErr("jim", entitySelector(SelectorRequirements.allowOne(Pig.class)));

        testOk(PIG_UUID.toString(), ENTITIES, (Predicate<EntitySelector<Entity>>) selector -> selector instanceof SingleEntitySelector<Entity>);
        testOk(PIG_UUID.toString(), ENTITIES, (Predicate<EntitySelector<Entity>>) selector -> selector.getFirst(null).getUniqueId().equals(PIG_UUID));

        testOk(ITEM_FRAME_UUID.toString(), ENTITIES, (Predicate<EntitySelector<Entity>>) selector -> selector instanceof SingleEntitySelector<Entity>);
        testOk(ITEM_FRAME_UUID.toString(), ENTITIES, (Predicate<EntitySelector<Entity>>) selector -> selector.getFirst(null).getUniqueId().equals(ITEM_FRAME_UUID));

        testErr(PIG_UUID.toString(), PLAYER);
        testErr(ITEM_FRAME_UUID.toString(), entitySelector(SelectorRequirements.allowOne(LivingEntity.class)));
    }

    @Test
    public void testMultiEntitySelector() {
        testOk("@p", ENTITIES);
        testOk("@a", ENTITIES);
        testOk("@e", ENTITIES);

        testOk("@p", ENTITY);
        testErr("@a", ENTITY);
        testErr("@e", ENTITY);

        testOk("@p", PLAYERS);
        testOk("@a", PLAYERS);
        testErr("@e", PLAYERS);

        testOk("@p", PLAYER);
        testErr("@a", PLAYER);
        testErr("@e", PLAYER);

        testErr("a", ENTITIES);
        testErr("@m", ENTITIES);
        testErr("@masdfadfad", ENTITIES);

        testOk("@e[]", ENTITIES);
        testOk("@e[limit=3]", ENTITIES);
        testOk("@e[sort=nearest]", ENTITIES);
        testOk("@e[limit=10,sort=arbitrary]", ENTITIES);
        testOk("@e[limit=10,sort=arbitrary] some extra characters", ENTITIES);
        testOk("@e[limit=10,]", ENTITIES);
        testOk("@e[limit=3,sort=nearest,name=\"jim\\, bob\",]", ENTITIES);

        testErr("@e[", ENTITIES);
        testErr("@e]", ENTITIES);
        testErr("@e[]a", ENTITIES);
        testErr("@e[limit=1", ENTITIES);
        testErr("@e[limit=1,,]", ENTITIES);
        testErr("@e[limit]", ENTITIES);
        testErr("@e[limit=]", ENTITIES);
        testErr("@e[limit=,]", ENTITIES);
        testErr("@e[limit==]", ENTITIES);
        testErr("@e[limit=hi]", ENTITIES);
        testErr("@e[limit=10,sort=some random sort,]", ENTITIES);

        testOk("@e[type=player]", PLAYERS);
        testOk("@e[type=player,limit=1]", PLAYER);
        testErr("@p[limit=3]", ENTITY);
        testErr("@p[type=axolotl]", PLAYER);
    }

    private <T> void testOk(String arg, @NotNull ArgumentType<T> type) {
        this.<T>testOk(arg, type, val -> true);
    }
    private <T> void testOk(String arg, @NotNull ArgumentType<T> type, T val) {
        Result<T, String> result = type.parse(new StringArgumentReader(arg));
        assertTrue(result.isOk());
        assertEquals(val, result.unwrap());
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

    private static class Person {
        private final String name;
        private final int age;
        private final boolean cool;
        private final School school;
        private final List<Person> friends;

        private Person(String name, int age, boolean cool, School school, List<Person> friends) {
            this.name = name;
            this.age = age;
            this.cool = cool;
            this.school = school;
            this.friends = friends;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Person person = (Person) o;

            if (age != person.age) return false;
            if (cool != person.cool) return false;
            if (!Objects.equals(name, person.name)) return false;
            if (!Objects.equals(school, person.school)) return false;
            return Objects.equals(friends, person.friends);
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + age;
            result = 31 * result + (cool ? 1 : 0);
            result = 31 * result + (school != null ? school.hashCode() : 0);
            result = 31 * result + (friends != null ? friends.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "Person{" +
                   "name='" + name + '\'' +
                   ", age=" + age +
                   ", cool=" + cool +
                   ", school=" + school +
                   ", friends=" + friends +
                   '}';
        }
    }

    private static class School {
        private final String name;
        private final SchoolType type;

        private School(String name, SchoolType type) {
            this.name = name;
            this.type = type;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            School school = (School) o;

            if (!Objects.equals(name, school.name)) return false;
            return type == school.type;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (type != null ? type.hashCode() : 0);
            return result;
        }
    }

    @SuppressWarnings("unused")
    private enum SchoolType {
        ELEMENTARY,
        MIDDLE,
        HIGH,
        COLLEGE,
    }
}
