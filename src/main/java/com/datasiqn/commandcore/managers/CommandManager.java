package com.datasiqn.commandcore.managers;

import com.datasiqn.commandcore.commands.Command;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {
    private final Map<String, Command> commandMap = new HashMap<>();

    public void registerCommand(String name, Command command) {
        commandMap.put(name, command);
    }

    public Command getCommand(String name) {
        return commandMap.get(name);
    }

    public boolean hasCommand(String name) {
        return commandMap.containsKey(name);
    }

    @UnmodifiableView
    public Map<String, Command> allCommands() {
        return Collections.unmodifiableMap(commandMap);
    }
}
