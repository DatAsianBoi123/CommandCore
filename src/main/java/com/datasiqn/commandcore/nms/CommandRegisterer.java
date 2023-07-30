package com.datasiqn.commandcore.nms;

import com.datasiqn.commandcore.command.Command;
import com.datasiqn.commandcore.command.builder.CommandBuilder;
import com.mojang.brigadier.context.CommandContext;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class CommandRegisterer {
    protected final String rootName;

    public CommandRegisterer(String rootName) {
        this.rootName = rootName;
    }

    public abstract void addCommand(CommandBuilder builder);

    public abstract void removeOld();

    public abstract void register();

    public abstract com.datasiqn.commandcore.command.CommandContext toContext(CommandContext<?> context, Command command);

    protected Pair<String, List<String>> splitCommand(@NotNull String input) {
        boolean foundSpace = false;
        int firstSpace = 0;
        int secondSpace = input.length();
        char[] charArray = input.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            if (charArray[i] == ' ') {
                if (!foundSpace) {
                    firstSpace = i;
                    foundSpace = true;
                    continue;
                }
                secondSpace = i;
                break;
            }
        }
        StringBuilder labelBuilder = new StringBuilder();
        StringBuilder argsBuilder = new StringBuilder();
        List<String> args = new ArrayList<>();
        boolean reachedSecondSpace = false;
        for (int i = firstSpace + 1; i < charArray.length; i++) {
            if (i == secondSpace) {
                reachedSecondSpace = true;
                continue;
            }
            if (reachedSecondSpace) {
                if (charArray[i] == ' ') {
                    args.add(argsBuilder.toString());
                    argsBuilder = new StringBuilder();
                    continue;
                }
                argsBuilder.append(charArray[i]);
            }
            else labelBuilder.append(charArray[i]);
        }
        args.add(argsBuilder.toString());
        String label = labelBuilder.toString();
        return Pair.of(label, args);
    }
}
