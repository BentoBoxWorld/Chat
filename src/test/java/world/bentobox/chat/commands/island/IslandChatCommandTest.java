package world.bentobox.chat.commands.island;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.chat.CommonTestSetup;
import world.bentobox.chat.listeners.ChatListener;

/**
 * Tests for the {@link IslandChatCommand} class.
 */
public class IslandChatCommandTest extends CommonTestSetup {

    @Mock
    private ChatListener chatListener;

    @Mock
    private User user;

    private IslandChatCommand cmd;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        when(addon.getListener()).thenReturn(chatListener);
        when(user.getUniqueId()).thenReturn(uuid);
        when(user.getPlayer()).thenReturn(player);
        when(user.getLocation()).thenReturn(location);
        cmd = new IslandChatCommand(addon, ic, "chat");
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testSetup() {
        assertEquals("bskyblock.chat.island-chat", cmd.getPermission());
        assertTrue(cmd.isOnlyPlayer());
        assertEquals("chat.island-chat.description", cmd.getDescription());
    }

    @Test
    public void testCanExecuteNoIsland() {
        when(im.getIslandAt(any())).thenReturn(Optional.empty());
        assertFalse(cmd.canExecute(user, "chat", Collections.emptyList()));
    }

    @Test
    public void testCanExecuteWithIsland() {
        when(im.getIslandAt(any())).thenReturn(Optional.of(island));
        assertTrue(cmd.canExecute(user, "chat", Collections.emptyList()));
    }

    @Test
    public void testExecuteToggleOn() {
        when(im.getIslandAt(any())).thenReturn(Optional.of(island));
        cmd.canExecute(user, "chat", Collections.emptyList()); // sets island field
        when(chatListener.toggleIslandChat(any(Island.class), eq(player))).thenReturn(true);
        assertTrue(cmd.execute(user, "chat", Collections.emptyList()));
        verify(user).sendMessage("chat.island-chat.island-on");
    }

    @Test
    public void testExecuteToggleOff() {
        when(im.getIslandAt(any())).thenReturn(Optional.of(island));
        cmd.canExecute(user, "chat", Collections.emptyList());
        when(chatListener.toggleIslandChat(any(Island.class), eq(player))).thenReturn(false);
        assertTrue(cmd.execute(user, "chat", Collections.emptyList()));
        verify(user).sendMessage("chat.island-chat.island-off");
    }

    @Test
    public void testExecuteWithArgs() {
        when(im.getIslandAt(any())).thenReturn(Optional.of(island));
        cmd.canExecute(user, "chat", Collections.emptyList());
        assertTrue(cmd.execute(user, "chat", List.of("hello", "island")));
        verify(chatListener).islandChat(any(Island.class), eq(player), eq("hello island"));
        verify(chatListener, never()).toggleIslandChat(any(), any());
    }

    @Test
    public void testExecuteWithSingleArg() {
        when(im.getIslandAt(any())).thenReturn(Optional.of(island));
        cmd.canExecute(user, "chat", Collections.emptyList());
        assertTrue(cmd.execute(user, "chat", List.of("hi")));
        verify(chatListener).islandChat(any(Island.class), eq(player), eq("hi"));
    }
}
