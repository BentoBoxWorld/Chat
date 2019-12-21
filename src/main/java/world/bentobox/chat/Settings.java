package world.bentobox.chat;

import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.ConfigObject;
import world.bentobox.bentobox.api.configuration.StoreAt;

import java.util.Arrays;
import java.util.List;

/**
 * Contains the config settings for this addon.
 * @author Poslovitch
 */
@StoreAt(filename = "config.yml", path = "addons/Chat")
@ConfigComment("Configuration file for Chat [version].")
public class Settings implements ConfigObject {

    @ConfigEntry(path = "team-chat.gamemodes")
    private List<String> teamChatGamemodes = Arrays.asList("BSkyBlock", "AcidIsland", "CaveBlock", "SkyGrid");

    @ConfigComment("Log team chats to console.")
    @ConfigEntry(path = "team-chat.log")
    private boolean logTeamChats;

    @ConfigComment("Lists the gamemodes in which you want the Chat addon to be effective.")
    @ConfigEntry(path = "island-chat.gamemodes")
    private List<String> islandChatGamemodes = Arrays.asList("BSkyBlock", "AcidIsland", "CaveBlock", "SkyGrid");

    @ConfigComment("Log island chats to console.")
    @ConfigEntry(path = "island-chat.log")
    private boolean logIslandChats;



    public List<String> getTeamChatGamemodes() {
        return teamChatGamemodes;
    }

    public void setTeamChatGamemodes(List<String> teamChatGamemodes) {
        this.teamChatGamemodes = teamChatGamemodes;
    }

    public List<String> getIslandChatGamemodes() {
        return islandChatGamemodes;
    }

    public void setIslandChatGamemodes(List<String> islandChatGamemodes) {
        this.islandChatGamemodes = islandChatGamemodes;
    }

    /**
     * @return the logTeamChats
     */
    public boolean isLogTeamChats() {
        return logTeamChats;
    }

    /**
     * @param logTeamChats the logTeamChats to set
     */
    public void setLogTeamChats(boolean logTeamChats) {
        this.logTeamChats = logTeamChats;
    }

    /**
     * @return the logIslandChats
     */
    public boolean isLogIslandChats() {
        return logIslandChats;
    }

    /**
     * @param logIslandChats the logIslandChats to set
     */
    public void setLogIslandChats(boolean logIslandChats) {
        this.logIslandChats = logIslandChats;
    }
}
