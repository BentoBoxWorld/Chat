package world.bentobox.chat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;

import org.bukkit.World;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import world.bentobox.bentobox.api.addons.AddonDescription;
import world.bentobox.bentobox.api.addons.GameModeAddon;

class ChatTest extends CommonTestSetup {

    private Chat chat;
    private GameModeAddon bskyblock;
    private World bskyblockOverWorld;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();
        chat = WhiteBox.newUninitializedInstance(Chat.class);
        WhiteBox.setInternalState(chat, "settings", settings);

        bskyblockOverWorld = mock(World.class);
        bskyblock = mock(GameModeAddon.class);
        AddonDescription desc = new AddonDescription.Builder("world.bentobox.bskyblock.BSkyBlock", "BSkyBlock", "1.0.0").build();
        when(bskyblock.getDescription()).thenReturn(desc);
        when(bskyblock.getOverWorld()).thenReturn(bskyblockOverWorld);
        when(am.getGameModeAddons()).thenReturn(List.of(bskyblock));
    }

    @Test
    void getWorldsFromExtra_emptyConfig_returnsEmpty() {
        when(settings.getExtraChatWorlds()).thenReturn(Map.of());

        List<World> result = chat.getWorldsFromExtra("any_world");

        assertTrue(result.isEmpty());
    }

    @Test
    void getWorldsFromExtra_exactMatch_returnsOverworld() {
        when(settings.getExtraChatWorlds()).thenReturn(Map.of("BSkyBlock", List.of("spawn_world")));

        List<World> result = chat.getWorldsFromExtra("spawn_world");

        assertEquals(List.of(bskyblockOverWorld), result);
    }

    @Test
    void getWorldsFromExtra_caseInsensitiveWorldName_returnsOverworld() {
        // Config lists "Spawn_World" but server names it "spawn_world"
        when(settings.getExtraChatWorlds()).thenReturn(Map.of("BSkyBlock", List.of("Spawn_World")));

        List<World> result = chat.getWorldsFromExtra("spawn_world");

        assertEquals(List.of(bskyblockOverWorld), result);
    }

    @Test
    void getWorldsFromExtra_noMatchingWorld_returnsEmpty() {
        when(settings.getExtraChatWorlds()).thenReturn(Map.of("BSkyBlock", List.of("spawn_world")));

        List<World> result = chat.getWorldsFromExtra("some_other_world");

        assertTrue(result.isEmpty());
    }

    @Test
    void getWorldsFromExtra_unknownGameMode_returnsEmpty() {
        // Config references a game mode that isn't registered
        when(settings.getExtraChatWorlds()).thenReturn(Map.of("UnknownMode", List.of("spawn_world")));

        List<World> result = chat.getWorldsFromExtra("spawn_world");

        assertTrue(result.isEmpty());
    }

    @Test
    void getWorldsFromExtra_multipleGameModes_returnsAllMatchingOverworlds() {
        GameModeAddon acidisland = mock(GameModeAddon.class);
        World acidOverWorld = mock(World.class);
        AddonDescription acidDesc = new AddonDescription.Builder("world.bentobox.acidisland.AcidIsland", "AcidIsland", "1.0.0").build();
        when(acidisland.getDescription()).thenReturn(acidDesc);
        when(acidisland.getOverWorld()).thenReturn(acidOverWorld);
        when(am.getGameModeAddons()).thenReturn(List.of(bskyblock, acidisland));

        when(settings.getExtraChatWorlds()).thenReturn(Map.of(
                "BSkyBlock", List.of("spawn_world"),
                "AcidIsland", List.of("spawn_world")));

        List<World> result = chat.getWorldsFromExtra("spawn_world");

        assertEquals(2, result.size());
        assertTrue(result.contains(bskyblockOverWorld));
        assertTrue(result.contains(acidOverWorld));
    }
}
