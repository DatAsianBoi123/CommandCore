import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

public class MockRecipe implements Recipe, Keyed {
    private final NamespacedKey key;
    private final ItemStack result;

    public MockRecipe(NamespacedKey key, ItemStack result) {
        this.key = key;
        this.result = result;
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @NotNull
    @Override
    public ItemStack getResult() {
        return result;
    }
}
