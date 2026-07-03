package world.bentobox.chat.commands.island;

import java.util.List;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.chat.Chat;

/**
 * @author tastybento
 */
public class IslandChatCommand extends CompositeCommand {

    public IslandChatCommand(Chat addon, CompositeCommand parent, String label) {
        super(addon, parent, label);
    }

    @Override
    public void setup() {
        this.setPermission("chat.island-chat");
        this.setDescription("chat.island-chat.description");
        this.setOnlyPlayer(true);
        setConfigurableRankCommand();
    }

    @Override
    public boolean canExecute(User user, String label, List<String> args) {
        // Command instances are shared across all players, so the resolved island must not be
        // cached on an instance field here (e.g. player A's island could otherwise leak into
        // player B's execute() call) - it is recomputed per-player in execute() instead.
        return this.getIslands().getIslandAt(user.getLocation()).isPresent();
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        Chat addon = this.getAddon();

        // Resolve the island fresh for this specific invocation, since this command
        // instance is shared by all players and must not rely on state set in canExecute().
        Island island = this.getIslands().getIslandAt(user.getLocation()).orElse(null);
        if (island == null) {
            return false;
        }

        // Send the message directly into island chat without the need of toggling it
        // if there is existence of more arguments
        if (!args.isEmpty()) {
            addon.getListener().islandChat(island, user.getPlayer(), String.join(" ", args));
            return true;
        }


        if (addon.getListener().toggleIslandChat(island, user.getPlayer())) {
            user.sendMessage("chat.island-chat.island-on");
        } else {
            user.sendMessage("chat.island-chat.island-off");
        }
        return true;
    }

}
