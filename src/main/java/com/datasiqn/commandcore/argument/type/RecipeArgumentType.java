package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.argument.ArgumentReader;
import com.datasiqn.resultapi.Result;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

class RecipeArgumentType implements ArgumentType<Recipe> {
    @Override
    public @NotNull String getName() {
        return "recipe";
    }

    @Override
    public @NotNull Result<Recipe, String> parse(@NotNull ArgumentReader reader) {
        return NAMESPACED_KEY.parse(reader).andThen(key -> Result.ofNullable(Bukkit.getRecipe(key), "no recipe exists with the key " + key));
    }

    @Override
    public @NotNull Class<Recipe> getArgumentClass() {
        return Recipe.class;
    }
}
