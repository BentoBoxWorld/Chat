package world.bentobox.chat;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import com.google.common.collect.ImmutableSet;

import world.bentobox.bentobox.BentoBox;
import world.bentobox.bentobox.api.addons.GameModeAddon;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.Notifier;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.bentobox.managers.AddonsManager;
import world.bentobox.bentobox.managers.IslandWorldManager;
import world.bentobox.bentobox.managers.IslandsManager;
import world.bentobox.bentobox.managers.LocalesManager;
import world.bentobox.bentobox.managers.PlaceholdersManager;

/**
 * Common test setup for Chat addon tests.
 * Provides a BentoBox plugin mock, common mocks, and MockBukkit server.
 * Don't forget to call {@code super.setUp()} and {@code super.tearDown()} in subclasses.
 */
public abstract class CommonTestSetup {

    protected UUID uuid = UUID.randomUUID();

    @Mock
    protected Chat addon;
    @Mock
    protected Player player;
    @Mock
    protected PluginManager pim;
    @Mock
    protected ItemFactory itemFactory;
    @Mock
    protected Location location;
    @Mock
    protected World world;
    @Mock
    protected IslandWorldManager iwm;
    @Mock
    protected IslandsManager im;
    @Mock
    protected Island island;
    @Mock
    protected BentoBox plugin;
    @Mock
    protected Notifier notifier;
    @Mock
    protected BukkitScheduler sch;
    @Mock
    protected LocalesManager lm;
    @Mock
    protected CompositeCommand ic;
    @Mock
    protected GameModeAddon gameModeAddon;
    @Mock
    protected AddonsManager am;
    @Mock
    protected Settings settings;

    protected ServerMock server;
    protected MockedStatic<Bukkit> mockedBukkit;
    protected AutoCloseable closeable;

    @BeforeEach
    public void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        server = MockBukkit.mock();

        // Set up BentoBox singleton
        WhiteBox.setInternalState(BentoBox.class, "instance", plugin);

        // Register the static mock for Bukkit
        mockedBukkit = Mockito.mockStatic(Bukkit.class, Mockito.RETURNS_DEEP_STUBS);
        mockedBukkit.when(Bukkit::getMinecraftVersion).thenReturn("1.21.11");
        mockedBukkit.when(Bukkit::getBukkitVersion).thenReturn("1.21.11-R0.1-SNAPSHOT");
        mockedBukkit.when(Bukkit::getPluginManager).thenReturn(pim);
        mockedBukkit.when(Bukkit::getItemFactory).thenReturn(itemFactory);
        mockedBukkit.when(Bukkit::getServer).thenReturn(server);
        mockedBukkit.when(() -> Bukkit.getScheduler()).thenReturn(sch);
        mockedBukkit.when(Bukkit::getOnlinePlayers).thenAnswer(invocation -> Collections.emptyList());

        // World
        when(world.toString()).thenReturn("world");
        when(world.getName()).thenReturn("BSkyBlock_world");

        // Location
        when(location.getWorld()).thenReturn(world);
        when(location.getBlockX()).thenReturn(0);
        when(location.getBlockY()).thenReturn(0);
        when(location.getBlockZ()).thenReturn(0);
        when(location.clone()).thenReturn(location);

        // Player
        when(player.getUniqueId()).thenReturn(uuid);
        when(player.getLocation()).thenReturn(location);
        when(player.getWorld()).thenReturn(world);
        when(player.getName()).thenReturn("tastybento");

        User.setPlugin(plugin);
        User.clearUsers();
        User.getInstance(player);

        // IWM
        when(plugin.getIWM()).thenReturn(iwm);
        when(iwm.inWorld(any(Location.class))).thenReturn(true);
        when(iwm.inWorld(any(World.class))).thenReturn(true);
        when(iwm.getFriendlyName(any())).thenReturn("BSkyBlock");

        // Island Manager
        when(plugin.getIslands()).thenReturn(im);
        when(im.getProtectedIslandAt(any())).thenReturn(Optional.of(island));
        when(island.getOwner()).thenReturn(uuid);
        when(island.getMemberSet()).thenReturn(ImmutableSet.of(uuid));
        when(island.onIsland(any())).thenReturn(true);

        // Locales & Placeholders
        when(lm.get(any(), any())).thenAnswer(
                (Answer<String>) invocation -> invocation.getArgument(1, String.class));
        PlaceholdersManager phm = mock(PlaceholdersManager.class);
        when(plugin.getPlaceholdersManager()).thenReturn(phm);
        when(phm.replacePlaceholders(any(), any())).thenAnswer(
                (Answer<String>) invocation -> invocation.getArgument(1, String.class));
        when(plugin.getLocalesManager()).thenReturn(lm);

        // Notifier
        when(plugin.getNotifier()).thenReturn(notifier);

        // BentoBox settings
        world.bentobox.bentobox.Settings bentoSettings = new world.bentobox.bentobox.Settings();
        when(plugin.getSettings()).thenReturn(bentoSettings);

        // AddonsManager
        when(plugin.getAddonsManager()).thenReturn(am);
        when(am.getGameModeAddons()).thenReturn(Collections.emptyList());

        // Addon
        when(addon.getPlugin()).thenReturn(plugin);
        when(addon.getIslands()).thenReturn(im);
        when(addon.getSettings()).thenReturn(settings);

        // Command
        when(ic.getAddon()).thenReturn(addon);
        when(ic.getPermissionPrefix()).thenReturn("bskyblock.");
        when(ic.getLabel()).thenReturn("island");
        when(ic.getTopLabel()).thenReturn("island");
        when(ic.getWorld()).thenReturn(world);

        // Settings
        when(settings.isLogTeamChats()).thenReturn(false);
        when(settings.isLogIslandChats()).thenReturn(false);
        when(settings.getEventPriority()).thenReturn(org.bukkit.event.EventPriority.NORMAL);

        // Addon commandsManager
        world.bentobox.bentobox.managers.CommandsManager cm = mock(
                world.bentobox.bentobox.managers.CommandsManager.class);
        when(plugin.getCommandsManager()).thenReturn(cm);
    }

    @AfterEach
    public void tearDown() throws Exception {
        mockedBukkit.closeOnDemand();
        closeable.close();
        MockBukkit.unmock();
        User.clearUsers();
        Mockito.framework().clearInlineMocks();
        deleteAll(new File("database"));
        deleteAll(new File("database_backup"));
    }

    protected static void deleteAll(File file) throws IOException {
        if (file.exists()) {
            Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
