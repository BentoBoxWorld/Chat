package world.bentobox.chat;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ChatPladdon} class.
 */
public class ChatPladdonTest extends CommonTestSetup {

    private ChatPladdon pladdon;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();
        pladdon = WhiteBox.newUninitializedInstance(ChatPladdon.class);
    }

    @Override
    @AfterEach
    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Test
    public void testGetAddonReturnsChat() {
        // Set a Chat instance via reflection to avoid classloader issues
        Chat chat = WhiteBox.newUninitializedInstance(Chat.class);
        WhiteBox.setInternalState(pladdon, "addon", chat);
        assertNotNull(pladdon.getAddon());
    }

    @Test
    public void testGetAddonReturnsSameInstance() {
        Chat chat = WhiteBox.newUninitializedInstance(Chat.class);
        WhiteBox.setInternalState(pladdon, "addon", chat);
        // Should return the same instance on repeated calls
        assertSame(pladdon.getAddon(), pladdon.getAddon());
    }
}
