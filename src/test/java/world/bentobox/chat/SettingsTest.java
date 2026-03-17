package world.bentobox.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Arrays;
import java.util.List;

import org.bukkit.event.EventPriority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for the {@link Settings} class.
 */
public class SettingsTest {

    private Settings settings;

    @BeforeEach
    public void setUp() {
        // Set up static addon reference required by getEventPriority
        Chat addonMock = mock(Chat.class);
        Chat.addon = addonMock;
        settings = new Settings();
    }

    @Test
    public void testDefaultTeamChatGamemodes() {
        List<String> defaults = settings.getTeamChatGamemodes();
        assertNotNull(defaults);
        assertTrue(defaults.contains("BSkyBlock"));
        assertTrue(defaults.contains("AcidIsland"));
        assertTrue(defaults.contains("CaveBlock"));
        assertTrue(defaults.contains("SkyGrid"));
    }

    @Test
    public void testSetTeamChatGamemodes() {
        List<String> newList = Arrays.asList("BSkyBlock", "MyAddon");
        settings.setTeamChatGamemodes(newList);
        assertEquals(newList, settings.getTeamChatGamemodes());
    }

    @Test
    public void testDefaultIslandChatGamemodes() {
        List<String> defaults = settings.getIslandChatGamemodes();
        assertNotNull(defaults);
        assertTrue(defaults.contains("BSkyBlock"));
        assertTrue(defaults.contains("AcidIsland"));
    }

    @Test
    public void testSetIslandChatGamemodes() {
        List<String> newList = Arrays.asList("BSkyBlock");
        settings.setIslandChatGamemodes(newList);
        assertEquals(newList, settings.getIslandChatGamemodes());
    }

    @Test
    public void testDefaultLogTeamChats() {
        assertFalse(settings.isLogTeamChats());
    }

    @Test
    public void testSetLogTeamChats() {
        settings.setLogTeamChats(true);
        assertTrue(settings.isLogTeamChats());
        settings.setLogTeamChats(false);
        assertFalse(settings.isLogTeamChats());
    }

    @Test
    public void testDefaultLogIslandChats() {
        assertFalse(settings.isLogIslandChats());
    }

    @Test
    public void testSetLogIslandChats() {
        settings.setLogIslandChats(true);
        assertTrue(settings.isLogIslandChats());
        settings.setLogIslandChats(false);
        assertFalse(settings.isLogIslandChats());
    }

    @Test
    public void testDefaultEventPriority() {
        // Default is "normal" which maps to NORMAL
        assertEquals(EventPriority.NORMAL, settings.getEventPriority());
    }

    @Test
    public void testSetEventPriorityEnum() {
        settings.setEventPriority(EventPriority.HIGH);
        assertEquals(EventPriority.HIGH, settings.getEventPriority());
    }

    @Test
    public void testSetEventPriorityString() {
        settings.setEventPriority("highest");
        assertEquals(EventPriority.HIGHEST, settings.getEventPriority());
    }

    @Test
    public void testSetEventPriorityStringLow() {
        settings.setEventPriority("low");
        assertEquals(EventPriority.LOW, settings.getEventPriority());
    }

    @Test
    public void testSetEventPriorityStringLowest() {
        settings.setEventPriority("lowest");
        assertEquals(EventPriority.LOWEST, settings.getEventPriority());
    }

    @Test
    public void testSetEventPriorityStringMonitor() {
        settings.setEventPriority("monitor");
        assertEquals(EventPriority.MONITOR, settings.getEventPriority());
    }

    @Test
    public void testSetEventPriorityStringInvalid() {
        // Invalid value should fall back to NORMAL and log an error
        settings.setEventPriority("invalid_priority");
        assertEquals(EventPriority.NORMAL, settings.getEventPriority());
    }

    @Test
    public void testDefaultChatGamemode() {
        assertEquals("", settings.getDefaultChatGamemode());
    }

    @Test
    public void testSetDefaultChatGamemode() {
        settings.setDefaultChatGamemode("BSkyBlock");
        assertEquals("BSkyBlock", settings.getDefaultChatGamemode());
    }
}
