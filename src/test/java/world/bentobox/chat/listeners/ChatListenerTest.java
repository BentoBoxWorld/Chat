package world.bentobox.chat.listeners;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.google.common.collect.ImmutableSet;

import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.chat.CommonTestSetup;

/**
 * Tests for the {@link ChatListener} class.
 */
@SuppressWarnings("deprecation")
public class ChatListenerTest extends CommonTestSetup {

    private ChatListener listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        when(addon.isRegisteredGameWorld(any(World.class))).thenReturn(true);
        when(addon.getChatWorld()).thenReturn(Optional.empty());
        listener = new ChatListener(addon);
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception {
        super.tearDown();
    }

    // -----------------------------------------------------------------------
    // togglePlayerTeamChat tests
    // -----------------------------------------------------------------------

    @Test
    public void testTogglePlayerTeamChatOn() {
        UUID uid = UUID.randomUUID();
        assertTrue(listener.togglePlayerTeamChat(uid),
                "First toggle should turn team chat ON");
    }

    @Test
    public void testTogglePlayerTeamChatOff() {
        UUID uid = UUID.randomUUID();
        listener.togglePlayerTeamChat(uid); // ON
        assertFalse(listener.togglePlayerTeamChat(uid),
                "Second toggle should turn team chat OFF");
    }

    @Test
    public void testIsTeamChatFalseByDefault() {
        assertFalse(listener.isTeamChat(UUID.randomUUID()));
    }

    @Test
    public void testIsTeamChatTrueAfterToggle() {
        UUID uid = UUID.randomUUID();
        listener.togglePlayerTeamChat(uid);
        assertTrue(listener.isTeamChat(uid));
    }

    // -----------------------------------------------------------------------
    // toggleSpy tests
    // -----------------------------------------------------------------------

    @Test
    public void testToggleSpyOn() {
        assertTrue(listener.toggleSpy(uuid));
    }

    @Test
    public void testToggleSpyOff() {
        listener.toggleSpy(uuid); // ON
        assertFalse(listener.toggleSpy(uuid));
    }

    // -----------------------------------------------------------------------
    // toggleIslandSpy tests
    // -----------------------------------------------------------------------

    @Test
    public void testToggleIslandSpyOn() {
        assertTrue(listener.toggleIslandSpy(uuid));
    }

    @Test
    public void testToggleIslandSpyOff() {
        listener.toggleIslandSpy(uuid); // ON
        assertFalse(listener.toggleIslandSpy(uuid));
    }

    // -----------------------------------------------------------------------
    // toggleIslandChat tests
    // -----------------------------------------------------------------------

    @Test
    public void testToggleIslandChatOn() {
        assertTrue(listener.toggleIslandChat(island, player));
    }

    @Test
    public void testToggleIslandChatOff() {
        listener.toggleIslandChat(island, player); // ON
        assertFalse(listener.toggleIslandChat(island, player));
    }

    // -----------------------------------------------------------------------
    // isChat tests
    // -----------------------------------------------------------------------

    @Test
    public void testIsChatFalseByDefault() {
        assertFalse(listener.isChat(uuid));
    }

    @Test
    public void testIsChatFalseWhenPlayerOffline() {
        // Player is not online in server mock by default for an unknown UUID
        assertFalse(listener.isChat(UUID.randomUUID()));
    }

    @Test
    public void testIsChatTrueAfterToggle() {
        mockedBukkit.when(() -> org.bukkit.Bukkit.getPlayer(uuid)).thenReturn(player);
        listener.toggleIslandChat(island, player);
        assertTrue(listener.isChat(uuid));
    }

    // -----------------------------------------------------------------------
    // onChat tests
    // -----------------------------------------------------------------------

    @Test
    public void testOnChatNotInRegisteredWorld() {
        // Not in a registered game world
        when(addon.isRegisteredGameWorld(any(World.class))).thenReturn(false);
        when(addon.getChatWorld()).thenReturn(Optional.empty());

        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, player, "Hello", Collections.emptySet());
        listener.onChat(event);
        assertFalse(event.isCancelled());
    }

    @Test
    public void testOnChatTeamChatActive() {
        // Player has team chat enabled and is in a team
        listener.togglePlayerTeamChat(uuid);
        when(im.inTeam(any(World.class), any(UUID.class))).thenReturn(true);

        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, player, "Hello team!", Collections.emptySet());
        listener.onChat(event);
        // Chat should be cancelled (intercepted for team chat)
        assertTrue(event.isCancelled());
    }

    @Test
    public void testOnChatTeamChatNotInTeam() {
        // Player has team chat enabled but is NOT in a team
        listener.togglePlayerTeamChat(uuid);
        when(im.inTeam(any(World.class), any(UUID.class))).thenReturn(false);
        when(im.getIslandAt(any())).thenReturn(Optional.empty());

        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, player, "Hello!", Collections.emptySet());
        listener.onChat(event);
        // Not cancelled because not in team
        assertFalse(event.isCancelled());
    }

    @Test
    public void testOnChatIslandChatActive() {
        // Player is in island chat
        listener.toggleIslandChat(island, player);
        when(im.getIslandAt(any())).thenReturn(Optional.of(island));

        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, player, "Hello island!", Collections.emptySet());
        listener.onChat(event);
        assertTrue(event.isCancelled());
    }

    @Test
    public void testOnChatIslandChatPlayerNotRegistered() {
        // Island is in the map but this player is not registered to island chat
        Player otherPlayer = mock(Player.class);
        when(otherPlayer.getUniqueId()).thenReturn(UUID.randomUUID());
        when(otherPlayer.getWorld()).thenReturn(world);
        when(otherPlayer.getLocation()).thenReturn(location);

        listener.toggleIslandChat(island, otherPlayer); // Register different player
        when(im.getIslandAt(any())).thenReturn(Optional.of(island));

        AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, player, "Hello!", Collections.emptySet());
        listener.onChat(event);
        // This player (uuid) is not in the island chatters set, so not cancelled
        assertFalse(event.isCancelled());
    }

    // -----------------------------------------------------------------------
    // teamChat tests
    // -----------------------------------------------------------------------

    @Test
    public void testTeamChatNoIsland() {
        when(im.getIsland(any(World.class), any(UUID.class))).thenReturn(null);
        // Should not throw, just return silently
        listener.teamChat(world, player, "test message");
        // No verification needed - just ensures no NPE
    }

    @Test
    public void testTeamChatWithIsland() {
        when(im.getIsland(any(World.class), any(UUID.class))).thenReturn(island);
        when(island.getMemberSet()).thenReturn(ImmutableSet.of(uuid));
        mockedBukkit.when(org.bukkit.Bukkit::getOnlinePlayers).thenReturn(Collections.emptyList());

        // Should not throw
        listener.teamChat(world, player, "team message");
    }

    @Test
    public void testTeamChatLogsWhenEnabled() {
        when(settings.isLogTeamChats()).thenReturn(true);
        when(im.getIsland(any(World.class), any(UUID.class))).thenReturn(island);
        when(island.getMemberSet()).thenReturn(ImmutableSet.of(uuid));
        mockedBukkit.when(org.bukkit.Bukkit::getOnlinePlayers).thenReturn(Collections.emptyList());

        listener.teamChat(world, player, "logged message");
        verify(addon).log("[Team Chat Log] tastybento: logged message");
    }

    @Test
    public void testTeamChatDoesNotLogWhenDisabled() {
        when(settings.isLogTeamChats()).thenReturn(false);
        when(im.getIsland(any(World.class), any(UUID.class))).thenReturn(island);
        when(island.getMemberSet()).thenReturn(ImmutableSet.of(uuid));
        mockedBukkit.when(org.bukkit.Bukkit::getOnlinePlayers).thenReturn(Collections.emptyList());

        listener.teamChat(world, player, "silent message");
        verify(addon, never()).log(any());
    }

    // -----------------------------------------------------------------------
    // islandChat tests
    // -----------------------------------------------------------------------

    @Test
    public void testIslandChatLogsWhenEnabled() {
        when(settings.isLogTeamChats()).thenReturn(true);
        mockedBukkit.when(org.bukkit.Bukkit::getOnlinePlayers).thenReturn(Collections.emptyList());

        listener.islandChat(island, player, "island log message");
        verify(addon).log("[Team Chat Log] tastybento: island log message");
    }

    @Test
    public void testIslandChatDoesNotLogWhenDisabled() {
        when(settings.isLogTeamChats()).thenReturn(false);
        mockedBukkit.when(org.bukkit.Bukkit::getOnlinePlayers).thenReturn(Collections.emptyList());

        listener.islandChat(island, player, "island silent message");
        verify(addon, never()).log(any());
    }

    @Test
    public void testIslandChatSendsToIslandMembers() {
        Player.Spigot spigot = mock(Player.Spigot.class);
        when(player.spigot()).thenReturn(spigot);
        mockedBukkit.when(org.bukkit.Bukkit::getOnlinePlayers).thenReturn(Collections.singletonList(player));

        // Should not throw and should complete without errors
        listener.islandChat(island, player, "hello island");
    }

    // -----------------------------------------------------------------------
    // Event handler tests
    // -----------------------------------------------------------------------

    @Test
    public void testOnLeaveRemovesTeamChatUser() {
        listener.togglePlayerTeamChat(uuid);
        assertTrue(listener.isTeamChat(uuid));

        world.bentobox.bentobox.api.events.team.TeamLeaveEvent leaveEvent =
                mock(world.bentobox.bentobox.api.events.team.TeamLeaveEvent.class);
        when(leaveEvent.getPlayerUUID()).thenReturn(uuid);
        listener.onLeave(leaveEvent);

        assertFalse(listener.isTeamChat(uuid));
    }

    @Test
    public void testOnKickRemovesTeamChatUser() {
        listener.togglePlayerTeamChat(uuid);
        assertTrue(listener.isTeamChat(uuid));

        world.bentobox.bentobox.api.events.team.TeamKickEvent kickEvent =
                mock(world.bentobox.bentobox.api.events.team.TeamKickEvent.class);
        when(kickEvent.getPlayerUUID()).thenReturn(uuid);
        listener.onKick(kickEvent);

        assertFalse(listener.isTeamChat(uuid));
    }
}
