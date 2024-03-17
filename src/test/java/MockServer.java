import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.*;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.SpawnCategory;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.*;
import org.bukkit.loot.LootTable;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.structure.StructureManager;
import org.bukkit.util.CachedServerIcon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@SuppressWarnings({"ConstantConditions", "deprecation", "Contract"})
public class MockServer implements Server {
    private final Set<Player> players;
    private final Set<Entity> entities;
    private final List<World> worlds;
    private final Map<NamespacedKey, MockRecipe> recipes;

    public MockServer(@NotNull Builder builder) {
        this.players = new HashSet<>(builder.players);
        this.entities = new HashSet<>(builder.entities);
        this.worlds = builder.worlds.stream().map(MockWorld::new).collect(Collectors.toList());
        this.recipes = builder.recipes;
    }

    @NotNull
    @Override
    public String getName() {
        return "Test";
    }

    @NotNull
    @Override
    public String getVersion() {
        return "";
    }

    @NotNull
    @Override
    public String getBukkitVersion() {
        return "";
    }

    @NotNull
    @Override
    public Collection<? extends Player> getOnlinePlayers() {
        return players;
    }

    @Override
    public int getMaxPlayers() {
        return 0;
    }

    @Override
    public int getPort() {
        return 0;
    }

    @Override
    public int getViewDistance() {
        return 0;
    }

    @Override
    public int getSimulationDistance() {
        return 0;
    }

    @NotNull
    @Override
    public String getIp() {
        return null;
    }

    @NotNull
    @Override
    public String getWorldType() {
        return null;
    }

    @Override
    public boolean getGenerateStructures() {
        return false;
    }

    @Override
    public int getMaxWorldSize() {
        return 0;
    }

    @Override
    public boolean getAllowEnd() {
        return false;
    }

    @Override
    public boolean getAllowNether() {
        return false;
    }

    @NotNull
    @Override
    public String getResourcePack() {
        return null;
    }

    @NotNull
    @Override
    public String getResourcePackHash() {
        return null;
    }

    @NotNull
    @Override
    public String getResourcePackPrompt() {
        return null;
    }

    @Override
    public boolean isResourcePackRequired() {
        return false;
    }

    @Override
    public boolean hasWhitelist() {
        return false;
    }

    @Override
    public void setWhitelist(boolean value) {

    }

    @Override
    public boolean isWhitelistEnforced() {
        return false;
    }

    @Override
    public void setWhitelistEnforced(boolean value) {

    }

    @NotNull
    @Override
    public Set<OfflinePlayer> getWhitelistedPlayers() {
        return null;
    }

    @Override
    public void reloadWhitelist() {

    }

    @Override
    public int broadcastMessage(@NotNull String message) {
        return 0;
    }

    @NotNull
    @Override
    public String getUpdateFolder() {
        return null;
    }

    @NotNull
    @Override
    public File getUpdateFolderFile() {
        return null;
    }

    @Override
    public long getConnectionThrottle() {
        return 0;
    }

    @Override
    public int getTicksPerAnimalSpawns() {
        return 0;
    }

    @Override
    public int getTicksPerMonsterSpawns() {
        return 0;
    }

    @Override
    public int getTicksPerWaterSpawns() {
        return 0;
    }

    @Override
    public int getTicksPerWaterAmbientSpawns() {
        return 0;
    }

    @Override
    public int getTicksPerWaterUndergroundCreatureSpawns() {
        return 0;
    }

    @Override
    public int getTicksPerAmbientSpawns() {
        return 0;
    }

    @Override
    public int getTicksPerSpawns(@NotNull SpawnCategory spawnCategory) {
        return 0;
    }

    @Nullable
    @Override
    public Player getPlayer(@NotNull String name) {
        return null;
    }

    @Nullable
    @Override
    public Player getPlayerExact(@NotNull String name) {
        for (Player player : players) {
            if (player.getName().equals(name)) return player;
        }
        return null;
    }

    @NotNull
    @Override
    public List<Player> matchPlayer(@NotNull String name) {
        return null;
    }

    @Nullable
    @Override
    public Player getPlayer(@NotNull UUID id) {
        for (Player player : players) {
            if (player.getUniqueId().equals(id)) return player;
        }
        return null;
    }

    @NotNull
    @Override
    public PluginManager getPluginManager() {
        return null;
    }

    @NotNull
    @Override
    public BukkitScheduler getScheduler() {
        return null;
    }

    @NotNull
    @Override
    public ServicesManager getServicesManager() {
        return null;
    }

    @NotNull
    @Override
    public List<World> getWorlds() {
        return new ArrayList<>(worlds);
    }

    @Nullable
    @Override
    public World createWorld(@NotNull WorldCreator creator) {
        return null;
    }

    @Override
    public boolean unloadWorld(@NotNull String name, boolean save) {
        return false;
    }

    @Override
    public boolean unloadWorld(@NotNull World world, boolean save) {
        return false;
    }

    @Nullable
    @Override
    public World getWorld(@NotNull String name) {
        for (World world : worlds) {
            if (world.getName().equalsIgnoreCase(name)) return world;
        }
        return null;
    }

    @Nullable
    @Override
    public World getWorld(@NotNull UUID uid) {
        return null;
    }

    @NotNull
    @Override
    public WorldBorder createWorldBorder() {
        return null;
    }

    @Nullable
    @Override
    public MapView getMap(int id) {
        return null;
    }

    @NotNull
    @Override
    public MapView createMap(@NotNull World world) {
        return null;
    }

    @NotNull
    @Override
    public ItemStack createExplorerMap(@NotNull World world, @NotNull Location location, @NotNull StructureType structureType) {
        return null;
    }

    @NotNull
    @Override
    public ItemStack createExplorerMap(@NotNull World world, @NotNull Location location, @NotNull StructureType structureType, int radius, boolean findUnexplored) {
        return null;
    }

    @Override
    public void reload() {

    }

    @Override
    public void reloadData() {

    }

    @NotNull
    @Override
    public Logger getLogger() {
        return Logger.getGlobal();
    }

    @Nullable
    @Override
    public PluginCommand getPluginCommand(@NotNull String name) {
        return null;
    }

    @Override
    public void savePlayers() {

    }

    @Override
    public boolean dispatchCommand(@NotNull CommandSender sender, @NotNull String commandLine) throws CommandException {
        return false;
    }

    @Override
    public boolean addRecipe(@Nullable Recipe recipe) {
        return false;
    }

    @NotNull
    @Override
    public List<Recipe> getRecipesFor(@NotNull ItemStack result) {
        return null;
    }

    @Nullable
    @Override
    public Recipe getRecipe(@NotNull NamespacedKey recipeKey) {
        return recipes.get(recipeKey);
    }

    @Nullable
    @Override
    public Recipe getCraftingRecipe(@NotNull ItemStack[] craftingMatrix, @NotNull World world) {
        return null;
    }

    @NotNull
    @Override
    public ItemStack craftItem(@NotNull ItemStack[] craftingMatrix, @NotNull World world, @NotNull Player player) {
        return null;
    }

    @NotNull
    @Override
    public Iterator<Recipe> recipeIterator() {
        return null;
    }

    @Override
    public void clearRecipes() {

    }

    @Override
    public void resetRecipes() {

    }

    @Override
    public boolean removeRecipe(@NotNull NamespacedKey key) {
        return false;
    }

    @NotNull
    @Override
    public Map<String, String[]> getCommandAliases() {
        return null;
    }

    @Override
    public int getSpawnRadius() {
        return 0;
    }

    @Override
    public void setSpawnRadius(int value) {

    }

    @Override
    public boolean shouldSendChatPreviews() {
        return false;
    }

    @Override
    public boolean isEnforcingSecureProfiles() {
        return false;
    }

    @Override
    public boolean getHideOnlinePlayers() {
        return false;
    }

    @Override
    public boolean getOnlineMode() {
        return false;
    }

    @Override
    public boolean getAllowFlight() {
        return false;
    }

    @Override
    public boolean isHardcore() {
        return false;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public int broadcast(@NotNull String message, @NotNull String permission) {
        return 0;
    }

    @NotNull
    @Override
    public OfflinePlayer getOfflinePlayer(@NotNull String name) {
        return null;
    }

    @NotNull
    @Override
    public OfflinePlayer getOfflinePlayer(@NotNull UUID id) {
        return null;
    }

    @NotNull
    @Override
    public PlayerProfile createPlayerProfile(@Nullable UUID uniqueId, @Nullable String name) {
        return null;
    }

    @NotNull
    @Override
    public PlayerProfile createPlayerProfile(@NotNull UUID uniqueId) {
        return null;
    }

    @NotNull
    @Override
    public PlayerProfile createPlayerProfile(@NotNull String name) {
        return null;
    }

    @NotNull
    @Override
    public Set<String> getIPBans() {
        return null;
    }

    @Override
    public void banIP(@NotNull String address) {

    }

    @Override
    public void unbanIP(@NotNull String address) {

    }

    @NotNull
    @Override
    public Set<OfflinePlayer> getBannedPlayers() {
        return null;
    }

    @NotNull
    @Override
    public BanList getBanList(@NotNull BanList.Type type) {
        return null;
    }

    @NotNull
    @Override
    public Set<OfflinePlayer> getOperators() {
        return null;
    }

    @NotNull
    @Override
    public GameMode getDefaultGameMode() {
        return null;
    }

    @Override
    public void setDefaultGameMode(@NotNull GameMode mode) {

    }

    @NotNull
    @Override
    public ConsoleCommandSender getConsoleSender() {
        return null;
    }

    @NotNull
    @Override
    public File getWorldContainer() {
        return null;
    }

    @NotNull
    @Override
    public OfflinePlayer[] getOfflinePlayers() {
        return new OfflinePlayer[0];
    }

    @NotNull
    @Override
    public Messenger getMessenger() {
        return null;
    }

    @NotNull
    @Override
    public HelpMap getHelpMap() {
        return null;
    }

    @NotNull
    @Override
    public Inventory createInventory(@Nullable InventoryHolder owner, @NotNull InventoryType type) {
        return null;
    }

    @NotNull
    @Override
    public Inventory createInventory(@Nullable InventoryHolder owner, @NotNull InventoryType type, @NotNull String title) {
        return null;
    }

    @NotNull
    @Override
    public Inventory createInventory(@Nullable InventoryHolder owner, int size) throws IllegalArgumentException {
        return null;
    }

    @NotNull
    @Override
    public Inventory createInventory(@Nullable InventoryHolder owner, int size, @NotNull String title) throws IllegalArgumentException {
        return null;
    }

    @NotNull
    @Override
    public Merchant createMerchant(@Nullable String title) {
        return null;
    }

    @Override
    public int getMaxChainedNeighborUpdates() {
        return 0;
    }

    @Override
    public int getMonsterSpawnLimit() {
        return 0;
    }

    @Override
    public int getAnimalSpawnLimit() {
        return 0;
    }

    @Override
    public int getWaterAnimalSpawnLimit() {
        return 0;
    }

    @Override
    public int getWaterAmbientSpawnLimit() {
        return 0;
    }

    @Override
    public int getWaterUndergroundCreatureSpawnLimit() {
        return 0;
    }

    @Override
    public int getAmbientSpawnLimit() {
        return 0;
    }

    @Override
    public int getSpawnLimit(@NotNull SpawnCategory spawnCategory) {
        return 0;
    }

    @Override
    public boolean isPrimaryThread() {
        return false;
    }

    @NotNull
    @Override
    public String getMotd() {
        return null;
    }

    @Nullable
    @Override
    public String getShutdownMessage() {
        return null;
    }

    @NotNull
    @Override
    public Warning.WarningState getWarningState() {
        return null;
    }

    @NotNull
    @Override
    public ItemFactory getItemFactory() {
        return null;
    }

    @Nullable
    @Override
    public ScoreboardManager getScoreboardManager() {
        return null;
    }

    @Nullable
    @Override
    public CachedServerIcon getServerIcon() {
        return null;
    }

    @NotNull
    @Override
    public CachedServerIcon loadServerIcon(@NotNull File file) {
        return null;
    }

    @NotNull
    @Override
    public CachedServerIcon loadServerIcon(@NotNull BufferedImage image) {
        return null;
    }

    @Override
    public void setIdleTimeout(int threshold) {

    }

    @Override
    public int getIdleTimeout() {
        return 0;
    }

    @NotNull
    @Override
    public ChunkGenerator.ChunkData createChunkData(@NotNull World world) {
        return null;
    }

    @NotNull
    @Override
    public BossBar createBossBar(@Nullable String title, @NotNull BarColor color, @NotNull BarStyle style, @NotNull BarFlag... flags) {
        return null;
    }

    @NotNull
    @Override
    public KeyedBossBar createBossBar(@NotNull NamespacedKey key, @Nullable String title, @NotNull BarColor color, @NotNull BarStyle style, @NotNull BarFlag... flags) {
        return null;
    }

    @NotNull
    @Override
    public Iterator<KeyedBossBar> getBossBars() {
        return null;
    }

    @Nullable
    @Override
    public KeyedBossBar getBossBar(@NotNull NamespacedKey key) {
        return null;
    }

    @Override
    public boolean removeBossBar(@NotNull NamespacedKey key) {
        return false;
    }

    @Nullable
    @Override
    public Entity getEntity(@NotNull UUID uuid) {
        for (Entity entity : entities) {
            if (entity.getUniqueId().equals(uuid)) return entity;
        }
        return null;
    }

    @Nullable
    @Override
    public Advancement getAdvancement(@NotNull NamespacedKey key) {
        return null;
    }

    @NotNull
    @Override
    public Iterator<Advancement> advancementIterator() {
        return null;
    }

    @NotNull
    @Override
    public BlockData createBlockData(@NotNull Material material) {
        return null;
    }

    @NotNull
    @Override
    public BlockData createBlockData(@NotNull Material material, @Nullable Consumer<BlockData> consumer) {
        return null;
    }

    @NotNull
    @Override
    public BlockData createBlockData(@NotNull String data) throws IllegalArgumentException {
        return null;
    }

    @NotNull
    @Override
    public BlockData createBlockData(@Nullable Material material, @Nullable String data) throws IllegalArgumentException {
        return null;
    }

    @Nullable
    @Override
    public <T extends Keyed> Tag<T> getTag(@NotNull String registry, @NotNull NamespacedKey tag, @NotNull Class<T> clazz) {
        return null;
    }

    @NotNull
    @Override
    public <T extends Keyed> Iterable<Tag<T>> getTags(@NotNull String registry, @NotNull Class<T> clazz) {
        return null;
    }

    @Nullable
    @Override
    public LootTable getLootTable(@NotNull NamespacedKey key) {
        return null;
    }

    @NotNull
    @Override
    public List<Entity> selectEntities(@NotNull CommandSender sender, @NotNull String selector) throws IllegalArgumentException {
        return null;
    }

    @NotNull
    @Override
    public StructureManager getStructureManager() {
        return null;
    }

    @Nullable
    @Override
    public <T extends Keyed> Registry<T> getRegistry(@NotNull Class<T> tClass) {
        return null;
    }

    @NotNull
    @Override
    public UnsafeValues getUnsafe() {
        return null;
    }

    @NotNull
    @Override
    public Spigot spigot() {
        return null;
    }

    @Override
    public void sendPluginMessage(@NotNull Plugin source, @NotNull String channel, byte @NotNull [] message) {

    }

    @NotNull
    @Override
    public Set<String> getListeningPluginChannels() {
        return null;
    }

    public static class Builder {
        private final Set<MockPlayer> players = new HashSet<>();
        private final Set<MockEntity> entities = new HashSet<>();
        private final Set<String> worlds = new HashSet<>();
        private final Map<NamespacedKey, MockRecipe> recipes = new HashMap<>();

        public Builder addPlayer(String name, UUID uuid) {
            players.add(new MockPlayer(name, uuid));
            return this;
        }

        public Builder addEntity(EntityType type, UUID uuid) {
            entities.add(new MockEntity(type, uuid));
            return this;
        }

        public Builder addWorld(String name) {
            worlds.add(name);
            return this;
        }

        public Builder addRecipe(MockRecipe recipe) {
            recipes.put(recipe.getKey(), recipe);
            return this;
        }

        public MockServer build() {
            return new MockServer(this);
        }
    }
}
