package world.bentobox.chat;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.World;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.chat.commands.admin.AdminIslandChatSpyCommand;
import world.bentobox.chat.commands.admin.AdminTeamChatSpyCommand;
import world.bentobox.chat.commands.island.IslandChatCommand;
import world.bentobox.chat.commands.island.IslandTeamChatCommand;
import world.bentobox.chat.listeners.ChatListener;

/**
 * Provides Team/Island-Chat related features, commands and options.
 * @author Poslovitch, tastybento
 */
public class Chat extends Addon {

    // Settings and configuration
    private Settings settings;
    private Config<Settings> configObject = new Config<>(this, Settings.class);
    private Set<GameModeAddon> registeredGameModes;
    private ChatListener listener;

    @Override
    public void onEnable() {
        /* Config */
        // Save default config from config.yml
        saveDefaultConfig();

        // Load settings from config.yml
        loadSettings();
        configObject.saveConfigObject(settings);

        /* Setup */
        setupCommands();

        // Register listener
        listener = new ChatListener(this);
        this.registerListener(listener);
    }

    private void setupCommands() {
        getPlugin().getAddonsManager().getGameModeAddons().forEach(gameModeAddon -> {
            if (settings.getTeamChatGamemodes().contains(gameModeAddon.getDescription().getName())) {
                log("Hooking team chat into " + gameModeAddon.getDescription().getName());
                gameModeAddon.getPlayerCommand().ifPresent(c -> new IslandTeamChatCommand(this, c, "teamchat"));
                gameModeAddon.getAdminCommand().ifPresent(c -> new AdminTeamChatSpyCommand(this, c, "teamchatspy"));
                registeredGameModes.add(gameModeAddon);
            }

            if (settings.getIslandChatGamemodes().contains(gameModeAddon.getDescription().getName())) {
                log("Hooking island chat into " + gameModeAddon.getDescription().getName());
                gameModeAddon.getPlayerCommand().ifPresent(playerCommand -> new IslandChatCommand(this, playerCommand, "chat"));
                gameModeAddon.getAdminCommand().ifPresent(playerCommand -> new AdminIslandChatSpyCommand(this, playerCommand, "chatspy"));
                registeredGameModes.add(gameModeAddon);
            }
        });
    }

    @Override
    public void onDisable() {
        // We shall do nothing (for now).
    }

    @Override
    public void onReload() {
        loadSettings();
    }

    /**
     * Check if chat is active in world
     * @param world - world to check
     * @return true if chat is active in this world
     */
    public boolean isRegisteredGameWorld(World world) {
        return registeredGameModes.parallelStream().anyMatch(gmw -> gmw.inWorld(world));
    }


    /**
     * Loads the Settings from the config.
     * Disables the addon in case it couldn't be done.
     */
    private void loadSettings() {
        registeredGameModes = new HashSet<>();
        settings = configObject.loadConfigObject();
        if (settings == null) {
            logError("Chat could not load the settings from its config.yml file. Disabling...");
            setState(State.DISABLED);
        }
    }

    public Settings getSettings() {
        return settings;
    }

    /**
     * @return the listener
     */
    public ChatListener getListener() {
        return listener;
    }

    /**
     * @param listener the listener to set
     */
    public void setListener(ChatListener listener) {
        this.listener = listener;
    }
}
