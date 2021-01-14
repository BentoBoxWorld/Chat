package world.bentobox.chat;

import java.util.Arrays;
import java.util.List;

import org.bukkit.event.EventPriority;

import world.bentobox.bentobox.api.configuration.ConfigComment;
import world.bentobox.bentobox.api.configuration.ConfigEntry;
import world.bentobox.bentobox.api.configuration.ConfigObject;
import world.bentobox.bentobox.api.configuration.StoreAt;

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

    @ConfigComment("Sets priority of AsyncPlayerChatEvent. Change this if Chat addon")
    @ConfigComment("is conflicting with other plugins which listen to the same event")
    @ConfigComment("Acceptable values: lowest, low, normal, high, highest, monitor")
    @ConfigEntry(path = "chat-listener.priority")
    private String eventPriority = "normal";

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

    public EventPriority getEventPriority() {

        EventPriority priority = EventPriority.NORMAL;

        try {
            priority = EventPriority.valueOf(this.eventPriority.toUpperCase());
        }
        catch (IllegalArgumentException e){
            Chat.addon.logError("EventPriority value: " + eventPriority + " is not valid in configuration. Using default: normal");
        }

        return priority;
    }

    public void setEventPriority(EventPriority eventPriority) {
        this.eventPriority = eventPriority.toString();
    }
}
