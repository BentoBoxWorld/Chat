package world.bentobox.chat.commands.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import world.bentobox.bentobox.api.user.User;
import world.bentobox.chat.CommonTestSetup;
import world.bentobox.chat.listeners.ChatListener;

/**
 * Tests for the {@link AdminIslandChatSpyCommand} class.
 */
public class AdminIslandChatSpyCommandTest extends CommonTestSetup {

    @Mock
    private ChatListener chatListener;

    @Mock
    private User user;

    private AdminIslandChatSpyCommand cmd;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        when(addon.getListener()).thenReturn(chatListener);
        when(user.getUniqueId()).thenReturn(uuid);
        cmd = new AdminIslandChatSpyCommand(addon, ic, "chatspy");
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testSetup() {
        assertEquals("bskyblock.chat.spy", cmd.getPermission());
        assertTrue(cmd.isOnlyPlayer());
        assertEquals("chat.island-chat.spy.description", cmd.getDescription());
    }

    @Test
    public void testExecuteToggleSpyOn() {
        when(chatListener.toggleIslandSpy(uuid)).thenReturn(true);
        assertTrue(cmd.execute(user, "chatspy", Collections.emptyList()));
        verify(user).sendMessage("chat.island-chat.spy.spy-on");
    }

    @Test
    public void testExecuteToggleSpyOff() {
        when(chatListener.toggleIslandSpy(uuid)).thenReturn(false);
        assertTrue(cmd.execute(user, "chatspy", Collections.emptyList()));
        verify(user).sendMessage("chat.island-chat.spy.spy-off");
    }
}
