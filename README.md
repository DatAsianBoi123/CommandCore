# CommandCore
CommandCore is a simple, yet powerful command framework for the Spigot API.

## Installation
**Java 8 or higher is required**

### 1) Installing to your local .m2 repo
**If you are using JitPack, you can skip this step.**

Clone this repository onto your local computer and run the command `mvn clean install` in that directory.
This will install CommandCore onto your local .m2 folder, so you can use the dependency.


### 2) Add the dependency

Add the CommandCore dependency

**If you are using JitPack, follow their instructions [here](https://jitpack.io/#DatAsianBoi123/CommandCore/)**
```xml
<dependency>
  <groupId>com.datasiqn</groupId>
  <artifactId>CommandCore</artifactId>
  <!-- Add the version you installed below -->
  <version>{version}</version>
  <scope>compile</scope>
</dependency>
```

Finally, add the maven shade plugin to shade in CommandCore
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

### 2) Creating a command

```java
import org.bukkit.entity.Player;

import com.datasiqn.commandcore.arguments.ArgumentType;
import com.datasiqn.commandcore.commands.Command;
import com.datasiqn.commandcore.commands.builder.*;

public class GreetCommand {
    // Make sure to use the com.datasiqn.commandcore.commands.Command import!!
    private final Command command = new CommandBuilder<>(Player.class)
            .description("Greets a player")
            .executes(sender -> sender.sendMessage("You ran this command with no arguments")) // Line 5
            .then(LiteralBuilder.<Player>literal("player")
                    .then(ArgumentBuilder.<Player, Player>argument(ArgumentType.PLAYER, "player")
                            .executes(context -> context.getArguments().get(1, ArgumentType.PLAYER).ifOk(player -> context.getSender().chat("Hello " + player.getName() + "!")))))
            .then(LiteralBuilder.<Player>literal("server")
                    .executes(context -> context.getSender().chat("Hello Server!")))
            .build();

    public Command getCommand() {
        return command;
    }
}
```
The `CommandBuilder` is what you use create commands.

The constructor argument tells CommandCore that only `Player`s may execute this command and won't let other senders execute it (command blocks, the console, etc.).

On the 5th line, we use a `.executes` call. This tells CommandCore to execute whatever is in the lambda when we run the command with no arguments.

On the next line, we make a `.then` call. It adds another 'branch' to the command 'tree'. You can think of the entire command as a tree, with the `CommandBuilder` as the main trunk. Any extra `.then` calls creates another branch, or path, that the user can go down.
When creating another branch, you need to supply it with a `CommandNode`. In this case, we give it a `LiteralBuilder`. A literal is a string that the user must type.
An example of a literal is Minecraft's `/time` command. After typing `/time`, you can either type `set`, `query`, or `add`. Those are all examples of a literal.

Notice how the literal `CommandNode` doesn't have an `.executes` call. This tells CommandCore that this specific branch cannot be executed without any further parameters.

On the next line, we create a new branch under the literal. This time, it is an argument. An argument is any string that the argument type can understand. In this case, we give it an argument type of `PLAYER`. This means that CommandCore will suggest us player names.

After that, we have a `.executes` call. This gets executed when we have typed that entire branch out. (ex. `/... player DatAsiqn`).

The next line is adding another literal onto the main trunk (notice the indentation). This literal is less complicated than the last, and just makes the sender chat "Hello Server!".

### 3) Registering the command
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
