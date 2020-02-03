package world.bentobox.chat.commands.island;

import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.bentobox.database.objects.Island;
import world.bentobox.chat.Chat;

/**
 * @author tastybento
 */
public class IslandChatCommand extends CompositeCommand {

    private @Nullable Island island;

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

        island = this.getIslands().getIslandAt(user.getLocation()).orElse(null);
        return island != null;
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        Chat addon = this.getAddon();

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
