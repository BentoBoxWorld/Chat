package world.bentobox.chat;

import sun.util.resources.cldr.ml.CalendarData_ml_IN;
import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.configuration.Config;
import world.bentobox.chat.commands.island.IslandChatCommand;

/**
 * Provides Team/Island-Chat related features, commands and options.
 * @author Poslovitch
 */
public class Chat extends Addon {

	// Settings and configuration
	private Settings settings;
	private Config<Settings> configObject = new Config<>(this, Settings.class);

	@Override
	public void onEnable() {
		/* Config */
		// Save default config from config.yml
		saveDefaultConfig();

		// Load settings from config.yml
		loadSettings();

		/* Setup */
		setupCommands();
	}

	private void setupCommands() {
		getPlugin().getAddonsManager().getGameModeAddons().forEach(gameModeAddon -> {
			if (settings.getTeamChatGamemodes().contains(gameModeAddon.getDescription().getName())) {
				// TODO
			}

			if (settings.getIslandChatGamemodes().contains(gameModeAddon.getDescription().getName())) {
				gameModeAddon.getPlayerCommand().ifPresent(playerCommand -> new IslandChatCommand(this, playerCommand));
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
	 * Loads the Settings from the config.
	 * Disables the addon in case it couldn't be done.
	 */
	private void loadSettings() {
		settings = configObject.loadConfigObject();
		if (settings == null) {
			logError("Chat could not load the settings from its config.yml file. Disabling...");
			setState(State.DISABLED);
		}
	}

	public Settings getSettings() {
		return settings;
	}
}
