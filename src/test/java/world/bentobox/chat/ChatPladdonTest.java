package world.bentobox.chat;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link ChatPladdon} class.
 */
public class ChatPladdonTest {

    @Test
    public void testGetAddonReturnsChat() {
        ChatPladdon pladdon = new ChatPladdon();
        assertNotNull(pladdon.getAddon());
        assertInstanceOf(Chat.class, pladdon.getAddon());
    }

    @Test
    public void testGetAddonReturnsSameInstance() {
        ChatPladdon pladdon = new ChatPladdon();
        // Should return the same instance on repeated calls
        assertSame(pladdon.getAddon(), pladdon.getAddon());
    }
}
