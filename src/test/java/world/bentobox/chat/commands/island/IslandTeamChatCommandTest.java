package world.bentobox.chat.commands.island;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.bukkit.World;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.chat.CommonTestSetup;
import world.bentobox.chat.listeners.ChatListener;

/**
 * Tests for the {@link IslandTeamChatCommand} class.
 */
public class IslandTeamChatCommandTest extends CommonTestSetup {

    @Mock
    private ChatListener chatListener;

    @Mock
    private User user;

    private IslandTeamChatCommand cmd;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        when(addon.getListener()).thenReturn(chatListener);
        when(user.getUniqueId()).thenReturn(uuid);
        when(user.getPlayer()).thenReturn(player);
        when(user.getWorld()).thenReturn(world);
        cmd = new IslandTeamChatCommand(addon, ic, "teamchat");
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testSetup() {
        assertEquals("bskyblock.chat.team-chat", cmd.getPermission());
        assertTrue(cmd.isOnlyPlayer());
        assertEquals("chat.team-chat.description", cmd.getDescription());
    }

    @Test
    public void testCanExecuteNoTeam() {
        when(im.inTeam(any(World.class), eq(uuid))).thenReturn(false);
        assertFalse(cmd.canExecute(user, "teamchat", Collections.emptyList()));
        verify(user).sendMessage("general.errors.no-team");
    }

    @Test
    public void testCanExecuteInTeam() {
        when(im.inTeam(any(World.class), eq(uuid))).thenReturn(true);
        assertTrue(cmd.canExecute(user, "teamchat", Collections.emptyList()));
        verify(user, never()).sendMessage(anyString());
    }

    @Test
    public void testExecuteToggleOn() {
        when(chatListener.togglePlayerTeamChat(uuid)).thenReturn(true);
        assertTrue(cmd.execute(user, "teamchat", Collections.emptyList()));
        verify(user).sendMessage("chat.team-chat.chat-on");
    }

    @Test
    public void testExecuteToggleOff() {
        when(chatListener.togglePlayerTeamChat(uuid)).thenReturn(false);
        assertTrue(cmd.execute(user, "teamchat", Collections.emptyList()));
        verify(user).sendMessage("chat.team-chat.chat-off");
    }

    @Test
    public void testExecuteWithArgs() {
        // When args are provided, send directly to team chat
        assertTrue(cmd.execute(user, "teamchat", List.of("hello", "world")));
        verify(chatListener).teamChat(any(World.class), eq(player), eq("hello world"));
        verify(chatListener, never()).togglePlayerTeamChat(any());
    }

    @Test
    public void testExecuteWithSingleArg() {
        assertTrue(cmd.execute(user, "teamchat", List.of("hi")));
        verify(chatListener).teamChat(any(World.class), eq(player), eq("hi"));
    }
}
