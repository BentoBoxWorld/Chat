package world.bentobox.chat.commands.island;

import java.util.List;

import world.bentobox.bentobox.api.addons.Addon;
import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.chat.Chat;

/**
 * Allows a player to mute/unmute team chat messages.
 * When muted, the player will not receive team chat messages.
 * @author tastybento
 */
public class IslandTeamMuteCommand extends CompositeCommand {

    public IslandTeamMuteCommand(Addon addon, CompositeCommand parent, String label) {
        super(addon, parent, label, "mtc");
    }

    @Override
    public void setup() {
        this.setPermission("chat.team-chat.mute");
        this.setDescription("chat.team-chat.mute.description");
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

        if (addon.getListener().toggleMuteTeamChat(user.getUniqueId())) {
            user.sendMessage("chat.team-chat.mute.mute-on");
        } else {
            user.sendMessage("chat.team-chat.mute.mute-off");
        }
        return true;
    }
}
