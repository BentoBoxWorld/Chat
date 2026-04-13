package world.bentobox.chat.requesthandlers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import world.bentobox.chat.CommonTestSetup;
import world.bentobox.chat.listeners.ChatListener;

/**
 * Tests for the {@link IsTeamChatHandler} class.
 */
public class IsTeamChatHandlerTest extends CommonTestSetup {

    @Mock
    private ChatListener chatListener;

    private IsTeamChatHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        when(addon.getListener()).thenReturn(chatListener);
        handler = new IsTeamChatHandler(addon);
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testHandleLabel() {
        assertEquals("isteamchat", handler.getLabel());
    }

    @Test
    public void testHandleNullMap() {
        assertFalse((Boolean) handler.handle(null));
    }

    @Test
    public void testHandleEmptyMap() {
        assertFalse((Boolean) handler.handle(new HashMap<>()));
    }

    @Test
    public void testHandleMissingUuid() {
        Map<String, Object> map = new HashMap<>();
        map.put("some_key", "some_value");
        assertFalse((Boolean) handler.handle(map));
    }

    @Test
    public void testHandleNullUuid() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", null);
        assertFalse((Boolean) handler.handle(map));
    }

    @Test
    public void testHandleWrongTypeUuid() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", "not-a-uuid-object");
        assertFalse((Boolean) handler.handle(map));
    }

    @Test
    public void testHandlePlayerNotInTeamChat() {
        UUID testUuid = UUID.randomUUID();
        when(chatListener.isTeamChat(testUuid)).thenReturn(false);
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", testUuid);
        assertFalse((Boolean) handler.handle(map));
    }

    @Test
    public void testHandlePlayerInTeamChat() {
        UUID testUuid = UUID.randomUUID();
        when(chatListener.isTeamChat(testUuid)).thenReturn(true);
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", testUuid);
        assertTrue((Boolean) handler.handle(map));
    }
}
