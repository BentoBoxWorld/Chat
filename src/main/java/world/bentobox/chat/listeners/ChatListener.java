package world.bentobox.chat.listeners;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import world.bentobox.bentobox.api.localization.TextVariables;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.RanksManager;
import world.bentobox.chat.Chat;

/**
 * This class is to implement team chat and island chat.
 * @author tastybento
 *
 */
public class ChatListener implements Listener {

    private final Chat addon;
    private final Set<UUID> teamChatUsers;
    private final Set<Island> islands;
    // List of which users are spying or not on team and island chat
    private final Set<UUID> spies;
    private final Set<UUID> islandSpies;

    public ChatListener(Chat addon) {
        this.teamChatUsers = new HashSet<>();
        this.islands = new HashSet<>();
        this.addon = addon;
        // Initialize spies
        spies = new HashSet<>();
        islandSpies = new HashSet<>();
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onChat(final AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        World w = e.getPlayer().getWorld();
        // Check world
        if (!addon.isRegisteredGameWorld(w)) {
            return;
        }
        if (teamChatUsers.contains(p.getUniqueId()) && addon.getIslands().inTeam(w, p.getUniqueId())) {
            // Cancel the event
            e.setCancelled(true);
            if (e.isAsynchronous()) {
                Bukkit.getScheduler().runTask(addon.getPlugin(), () -> teamChat(p,e.getMessage()));
            } else {
                teamChat(p, e.getMessage());
            }
        }
        addon.getIslands().getIslandAt(p.getLocation()).filter(islands::contains).ifPresent(i -> {
            // Cancel the event
            e.setCancelled(true);
            if (e.isAsynchronous()) {
                Bukkit.getScheduler().runTask(addon.getPlugin(), () -> islandChat(i, p, e.getMessage()));
            } else {
                islandChat(i, p, e.getMessage());
            }
        });
    }

    private void islandChat(Island i, Player player, String message) {
        Bukkit.getOnlinePlayers().stream().map(User::getInstance)
        .filter(u -> i.onIsland(u.getLocation()))
        // Send message to island
        .forEach(u -> u.sendMessage("chat.island-chat.syntax", TextVariables.NAME, player.getName(), "[message]", message));
        // Log if required
        if (addon.getSettings().isLogTeamChats()) {
            addon.log("[Team Chat Log] " + player.getName() + ": " + message);
        }
        // Spy if required
        Bukkit.getOnlinePlayers().stream()
        .filter(p -> islandSpies.contains(p.getUniqueId()))
        .map(User::getInstance)
        .forEach(a -> a.sendMessage("chat.island-chat.spy.syntax", TextVariables.NAME, player.getName(), "[message]", message));
    }

    private void teamChat(final Player player, String message) {
        // Get island members of coop or above
        addon.getIslands().getIsland(player.getWorld(), player.getUniqueId()).getMemberSet(RanksManager.COOP_RANK).stream()
        // Map to users
        .map(User::getInstance)
        // Filter for online only
        .filter(User::isOnline)
        // Send the message to them
        .forEach(target -> target.sendMessage("chat.team-chat.syntax", TextVariables.NAME, player.getName(), "[message]", message));
        // Log if required
        if (addon.getSettings().isLogTeamChats()) {
            addon.log("[Team Chat Log] " + player.getName() + ": " + message);
        }
        // Spy if required
        Bukkit.getOnlinePlayers().stream()
        .filter(p -> spies.contains(p.getUniqueId()))
        .map(User::getInstance)
        .forEach(u -> u.sendMessage("chat.team-chat.spy.syntax", TextVariables.NAME, player.getName(), "[message]", message));
    }

    /**
     * Whether the player has team chat on or not
     * @param playerUUID - the player's UUID
     * @return true if team chat is on
     */
    public boolean isTeamChat(UUID playerUUID) {
        return this.teamChatUsers.contains(playerUUID);
    }

    /**
     * Toggles team chat spy. Spy must also have the spy permission to see chats
     * @param playerUUID - the player's UUID
     * @return true if toggled on, false if toggled off
     */
    public boolean toggleSpy(UUID playerUUID) {
        if (spies.contains(playerUUID)) {
            spies.remove(playerUUID);
            return false;
        } else {
            spies.add(playerUUID);
            return true;
        }
    }

    /**
     * Toggles island chat spy. Spy must also have the spy permission to see chats
     * @param playerUUID - the player's UUID
     * @return true if toggled on, false if toggled off
     */
    public boolean toggleIslandSpy(UUID playerUUID) {
        if (islandSpies.contains(playerUUID)) {
            islandSpies.remove(playerUUID);
            return false;
        } else {
            islandSpies.add(playerUUID);
            return true;
        }
    }

    /**
     * Toggle player's team chat state
     * @param playerUUID - player's uuid
     * @return true if team chat is now on, otherwise false
     */
    public boolean togglePlayerTeamChat(UUID playerUUID) {
        if (teamChatUsers.contains(playerUUID)) {
            teamChatUsers.remove(playerUUID);
            return false;
        } else {
            teamChatUsers.add(playerUUID);
            return true;
        }

    }

    /**
     * Toggle island chat state
     * @param island - island
     * @return true if island chat is now on, otherwise false
     */
    public boolean toggleIslandChat(Island island) {
        if (islands.contains(island)) {
            islands.remove(island);
            return false;
        } else {
            islands.add(island);
            return true;
        }

    }
}
