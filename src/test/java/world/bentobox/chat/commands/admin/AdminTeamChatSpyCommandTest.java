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
 * Tests for the {@link AdminTeamChatSpyCommand} class.
 */
public class AdminTeamChatSpyCommandTest extends CommonTestSetup {

    @Mock
    private ChatListener chatListener;

    @Mock
    private User user;

    private AdminTeamChatSpyCommand cmd;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        when(addon.getListener()).thenReturn(chatListener);
        when(user.getUniqueId()).thenReturn(uuid);
        cmd = new AdminTeamChatSpyCommand(addon, ic, "teamchatspy");
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
        assertEquals("chat.team-chat.spy.description", cmd.getDescription());
    }

    @Test
    public void testExecuteToggleSpyOn() {
        when(chatListener.toggleSpy(uuid)).thenReturn(true);
        assertTrue(cmd.execute(user, "teamchatspy", Collections.emptyList()));
        verify(user).sendMessage("chat.team-chat.spy.spy-on");
    }

    @Test
    public void testExecuteToggleSpyOff() {
        when(chatListener.toggleSpy(uuid)).thenReturn(false);
        assertTrue(cmd.execute(user, "teamchatspy", Collections.emptyList()));
        verify(user).sendMessage("chat.team-chat.spy.spy-off");
    }
}
