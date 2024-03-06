package com.datasiqn.commandcore.argument.type;

import com.datasiqn.commandcore.command.CommandContext;
import com.datasiqn.resultapi.None;
import com.datasiqn.resultapi.Result;
import com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

class OfflinePlayerArgumentType implements SimpleArgumentType<CompletableFuture<OfflinePlayer>> {
    @Override
    public @NotNull String getName() {
        return "player";
    }

    @Override
    public @NotNull Result<CompletableFuture<OfflinePlayer>, None> parseWord(String word) {
        return Result.ofNullable((OfflinePlayer) Bukkit.getPlayerExact(word), None.NONE)
                .map(CompletableFuture::completedFuture)
                .or(Result.ok(CompletableFuture.supplyAsync(() -> Bukkit.getOfflinePlayer(word))));
    }

    @Override
    public @NotNull List<String> getTabComplete(@NotNull CommandContext context) {
        return ONLINE_PLAYER.getTabComplete(context);
    }

    @Override
    public @NotNull Class<CompletableFuture<OfflinePlayer>> getArgumentClass() {
        //noinspection unchecked,UnstableApiUsage
        return (Class<CompletableFuture<OfflinePlayer>>) new TypeToken<CompletableFuture<OfflinePlayer>>() {}.getRawType();
    }
}
