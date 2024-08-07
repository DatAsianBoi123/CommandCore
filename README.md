# CommandCore
CommandCore is a simple, yet powerful command framework for the Spigot API.

## Features
- Simple and intuitive
- Support for custom, user-created argument types
- Extendable command API that allows the creation of custom nodes
- Support for argument types with multiple spaces
- Auto-generation of a help command
- Builder-style command creation

## Installation
**Java 8 or higher is required**

Add the CommandCore dependency

```xml
<dependency>
  <groupId>com.datasiqn</groupId>
  <artifactId>CommandCore</artifactId>
  <!-- Add the version you installed below -->
  <version>{version}</version>
  <scope>compile</scope>
</dependency>
```

Then, add the maven shade plugin to shade in CommandCore
```xml
<build>
  <plugins>
      <plugin>
          <groupId>org.apache.maven.plugins</groupId>
          <artifactId>maven-shade-plugin</artifactId>
          <version>3.2.1</version>
          <executions>
              <execution>
                  <phase>package</phase>
                  <goals>
                      <goal>shade</goal>
                  </goals>
              </execution>
          </executions>
      </plugin>
  </plugins>
</build>
```

## Usage
### 1) Initialize CommandCore in your plugin

```java
import com.datasiqn.commandcore.CommandCore;

public final class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Init CommandCore
        CommandCore.init(this, "myrootcommand");
        // ...
    }
}
```
Using the static method `CommandCore.init(...)` initializes CommandCore. After doing this, you can access the instance from anywhere by using `CommandCore.getInstance()`

### 2) Create a command

```java
import com.datasiqn.commandcore.argument.type.ArgumentType;
import com.datasiqn.commandcore.command.builder.*;

public class GreetCommand {
    public CommandBuilder getCommand() {
        return new CommandBuilder()
                .description("Greets a player")
                .executes(context -> context.getSource().sendMessage("You ran this command with no arguments")) // Line 5
                .then(LiteralBuilder.literal("player")
                        .then(ArgumentBuilder.argument(ArgumentType.PLAYER, "player")
                                .requiresPlayer()
                                .executes(context -> context.getSource().getPlayer().chat("Hello " + context.getArguments().get(1, ArgumentType.PLAYER).getName()))))
                .then(LiteralBuilder.literal("server")
                        .requiresPlayer()
                        .executes(context -> context.getSource().getPlayer().chat("Hello Server!")));
    }
}
```
The `CommandBuilder` is what you use create commands.

On the 5th line, we use a `.executes` call. This tells CommandCore to execute whatever is in the lambda when we run the command with no arguments.

On the next line, we make a `.then` call. It adds another 'branch' to the command 'tree'. You can think of the entire command as a tree, with the `CommandBuilder` as the main trunk. Any extra `.then` calls creates another branch, or path, that the user can go down.
When creating another branch, you need to supply it with a `CommandNode`. In this case, we give it a `LiteralBuilder`. A literal is a string that the user must type.
An example of a literal is Minecraft's `/time` command. After typing `/time`, you can either type `set`, `query`, or `add`. Those are all examples of a literal.

Notice how the literal `CommandNode` doesn't have an `.executes` call. This tells CommandCore that this specific branch cannot be executed without any further parameters.

On the next line, we create a new branch under the literal. This time, it is an argument. An argument is any string that the argument type can understand. In this case, we give it an argument type of `PLAYER`. This means that CommandCore will suggest us player names.

After that, we have a `.requiresPlayer` call. This tells CommandCore that the current node (the PLAYER argument) requires a player to use it. If, for example, the console sends it, CommandCore will not execute the command and instead give the user an error.
Finally, we have a `.executes` call. This gets executed when we have typed that entire branch out. (ex. `/... player DatAsiqn`).

The next line is adding another literal onto the main trunk (notice the indentation). This literal is less complicated than the last, and just makes the sender chat "Hello Server!".

### 3) Register the command
```java
public final class MyPlugin extends JavaPlugin {
  @Override
  public void onEnable() {
    // Init CommandCore
    CommandCore.init(this, "myrootcommand");
    
    CommandManager commandManager = CommandCore.getInstance().getCommandManager();
    
    commandManager.registerCommand("greet", new GreetCommand().getCommand());
  }
}
```
This just registers the command, so it appears when we type `/myrootcommand`

Now, lets see this baby in action!

<img src=https://user-images.githubusercontent.com/55264711/197649001-c165521c-7153-44bc-9827-7d7da41a9360.gif width=500px />

## Contributing
You can contribute to this project by
* Creating an [issue](https://github.com/DatAsianBoi123/CommandCore/issues/new)
* [Forking](https://github.com/DatAsianBoi123/CommandCore/fork) this repo
* Creating a [pull request](https://github.com/DatAsianBoi123/CommandCore/compare)
---
Have any questions? Ask me on [discord](https://discord.com)! My tag is `datasianboi123`
