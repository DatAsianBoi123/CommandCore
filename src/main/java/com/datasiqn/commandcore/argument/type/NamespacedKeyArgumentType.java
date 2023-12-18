package com.datasiqn.commandcore.argument.type;

import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

class NamespacedKeyArgumentType implements SimpleArgumentType<NamespacedKey> {
    @Override
    public @NotNull String getName() {
        return "namespaced key";
    }

    @Override
    public @NotNull Result<NamespacedKey, None> parseWord(String word) {
        return Result.ofNullable(NamespacedKey.fromString(word), None.NONE);
    }

    @Override
    public @NotNull Class<NamespacedKey> getArgumentClass() {
        return NamespacedKey.class;
    }
}
