package world.bentobox.chat.commands.island;

import java.util.List;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.chat.Chat;

/**
 * @author tastybento
 */
public class IslandTeamChatCommand extends CompositeCommand {

    public IslandTeamChatCommand(Addon addon, CompositeCommand parent, String label) {
        super(addon, parent, label);
    }

    @Override
    public void setup() {
        this.setPermission("chat.team-chat");
        this.setDescription("chat.team-chat.description");
        this.setOnlyPlayer(true);
        setConfigurableRankCommand();
    }

    @Override
    public boolean canExecute(User user, String label, List<String> args) {
        boolean hasTeam = this.getIslands().inTeam(getWorld(), user.getUniqueId());
        if (!hasTeam) {
            user.sendMessage("general.errors.no-team");
        }
        return hasTeam;
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        Chat addon = this.getAddon();
        if (addon.getListener().togglePlayerTeamChat(user.getUniqueId())) {
            user.sendMessage("chat.team-chat.on");
        } else {
            user.sendMessage("chat.team-chat.off");
        }
        return true;
    }
}
