package world.bentobox.chat.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import org.bukkit.plugin.EventExecutor;
import world.bentobox.bentobox.api.events.team.TeamKickEvent;
import world.bentobox.bentobox.api.events.team.TeamLeaveEvent;
import world.bentobox.bentobox.api.localization.TextVariables;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.chat.Chat;

/**
 * This class is to implement team chat and island chat.
 * @author tastybento
 *
 */
public class ChatListener implements Listener, EventExecutor {

    private static final String MESSAGE = "[message]";
    private final Chat addon;
    private final Set<UUID> teamChatUsers;
    private final Map<Island, Set<Player>> islandChatters;
    // List of which users are spying or not on team and island chat
    private final Set<UUID> spies;
    private final Set<UUID> islandSpies;
    // List of which users have muted team chat
    private final Set<UUID> teamChatMuted;

    public ChatListener(Chat addon) {
        this.teamChatUsers = new HashSet<>();
        this.islandChatters = new HashMap<>();
        this.addon = addon;
        // Initialize spies
        spies = new HashSet<>();
        islandSpies = new HashSet<>();
        // Initialize muted
        teamChatMuted = new HashSet<>();
    }

    @Override
    public void execute(Listener listener, Event e) {

        // Needs to be checked, as we manually registered the listener
        // It's a replacement for ignoreCannceled = true
        if (((AsyncPlayerChatEvent) e).isCancelled())
            return;

        // Call the event method
        onChat((AsyncPlayerChatEvent) e);
    }

    private boolean handleChatSync(Player p, String message) {
        boolean handled = false;
        World playerWorld = p.getWorld();

        // Determine the worlds to use for team chat
        List<World> teamChatWorlds = new ArrayList<>();
        if (addon.isRegisteredGameWorld(playerWorld)) {
            teamChatWorlds.add(playerWorld);
        } else {
            // Check extra chat worlds config
            teamChatWorlds.addAll(addon.getWorldsFromExtra(playerWorld.getName()));
            // If no extra worlds matched, check default chat world
            if (teamChatWorlds.isEmpty()) {
                addon.getChatWorld().ifPresent(teamChatWorlds::add);
            }
        }

        // Process team chat for all matching worlds.
        // If multiple game modes cover the same extra world, chat goes to all matching teams.
        if (!teamChatWorlds.isEmpty() && teamChatUsers.contains(p.getUniqueId())) {
            for (World w : teamChatWorlds) {
                if (addon.getIslands().inTeam(w, p.getUniqueId())) {
                    handled = true;
                    teamChat(w, p, message);
                }
            }
        }

        // Island chat - uses physical location, only meaningful if player is on an island
        Island island = addon.getIslands().getIslandAt(p.getLocation())
                .filter(islandChatters.keySet()::contains)
                .filter(i -> islandChatters.get(i).contains(p))
                .orElse(null);
        if (island != null) {
            handled = true;
            islandChat(island, p, message);
        }

        return handled;
    }

    public void onChat(final AsyncPlayerChatEvent e) {

        Player p = e.getPlayer();
        String message = e.getMessage();

        if (e.isAsynchronous()) {
            try {
                Boolean handled = Bukkit.getScheduler().callSyncMethod(addon.getPlugin(),
                        () -> handleChatSync(p, message)).get();
                if (Boolean.TRUE.equals(handled)) {
                    e.setCancelled(true);
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            } catch (java.util.concurrent.ExecutionException ex) {
                addon.logError("Failed to process async chat for " + p.getName() + ": " + ex.getCause());
            }
        } else if (handleChatSync(p, message)) {
            e.setCancelled(true);
        }
    }

    // Removes player from TeamChat set if he left the island
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onLeave(TeamLeaveEvent e) {
        teamChatUsers.remove(e.getPlayerUUID());
        teamChatMuted.remove(e.getPlayerUUID());
    }

    // Removes player from TeamChat set if he was kicked from the island
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onKick(TeamKickEvent e) {
        teamChatUsers.remove(e.getPlayerUUID());
        teamChatMuted.remove(e.getPlayerUUID());
    }

    public void islandChat(Island i, Player player, String message) {
        Bukkit.getOnlinePlayers().stream().map(User::getInstance)
        .filter(u -> i.onIsland(u.getLocation()))
        // Send message to island
        .forEach(u -> u.sendMessage("chat.island-chat.syntax", TextVariables.NAME, player.getName(), MESSAGE, message));
        // Log if required
        if (addon.getSettings().isLogIslandChats()) {
            addon.log("[Island Chat Log] " + player.getName() + ": " + message);
        }
        // Spy if required
        Bukkit.getOnlinePlayers().stream()
        .filter(p -> islandSpies.contains(p.getUniqueId()))
        .map(User::getInstance)
        .forEach(a -> a.sendMessage("chat.island-chat.spy.syntax", TextVariables.NAME, player.getName(), MESSAGE, message));
    }

    public void teamChat(World w, final Player player, String message) {
        // Get island members of member or above
        Island island = addon.getIslands().getIsland(w, player.getUniqueId());
        if (island == null) {
            return;
        }
        island.getMemberSet().stream()
        // Map to users
        .map(User::getInstance)
        // Filter for online only
        .filter(User::isOnline)
        // Filter out muted players
        .filter(target -> !teamChatMuted.contains(target.getUniqueId()))
        // Send the message to them
        .forEach(target -> target.sendMessage("chat.team-chat.syntax", TextVariables.NAME, player.getName(), MESSAGE, message));
        // Always show the sender their own message if they have muted team chat, plus a reminder
        if (teamChatMuted.contains(player.getUniqueId())) {
            User sender = User.getInstance(player);
            sender.sendMessage("chat.team-chat.syntax", TextVariables.NAME, player.getName(), MESSAGE, message);
            sender.sendMessage("chat.team-chat.mute.reminder");
        }
        // Log if required
        if (addon.getSettings().isLogTeamChats()) {
            addon.log("[Team Chat Log] " + player.getName() + ": " + message);
        }
        // Spy if required
        Bukkit.getOnlinePlayers().stream()
        .filter(p -> spies.contains(p.getUniqueId()))
        .map(User::getInstance)
        .forEach(u -> u.sendMessage("chat.team-chat.spy.syntax", TextVariables.NAME, player.getName(), MESSAGE, message));
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
     * Whether the player has chat on or not. Note that with multiple islands the response is true
     * if *any* chat is enabled
     * @param playerUUID - player's UUID
     * @return true if chat is active on any island
     */
    public boolean isChat(UUID playerUUID) {
        Player p = Bukkit.getPlayer(playerUUID);
        if (p == null) {
            return false;
        }
        return this.islandChatters.values().stream().anyMatch(playerSet -> playerSet.contains(p));
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
            teamChatMuted.remove(playerUUID); // clear mute when team chat is toggled off
            return false;
        } else {
            teamChatUsers.add(playerUUID);
            return true;
        }

    }

    /**
     * Toggle island chat state
     * @param island - island
     * @param player - player
     * @return true if island chat is now on, otherwise false
     */
    public boolean toggleIslandChat(Island island, Player player) {
        var chatters = islandChatters.computeIfAbsent(island, k -> new HashSet<>());

        if (chatters.contains(player)) {
            chatters.remove(player);
            return false;
        } else {
            chatters.add(player);
            return true;
        }
    }

    /**
     * Toggle player's team chat mute state
     * @param playerUUID - player's uuid
     * @return true if team chat is now muted, otherwise false
     */
    public boolean toggleMuteTeamChat(UUID playerUUID) {
        if (teamChatMuted.contains(playerUUID)) {
            teamChatMuted.remove(playerUUID);
            return false;
        } else {
            teamChatMuted.add(playerUUID);
            return true;
        }
    }

    /**
     * Whether the player has muted team chat or not
     * @param playerUUID - the player's UUID
     * @return true if team chat is muted
     */
    public boolean isMutedTeamChat(UUID playerUUID) {
        return this.teamChatMuted.contains(playerUUID);
    }

}
