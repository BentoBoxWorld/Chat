package world.bentobox.chat.commands.admin;

import java.util.List;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.chat.Chat;

public class AdminTeamChatSpyCommand extends CompositeCommand {

    public AdminTeamChatSpyCommand(Chat addon, CompositeCommand c, String label) {
        super(addon, c, label);
    }

    @Override
    public void setup() {
        this.setPermission("chat.spy");
        this.setDescription("chat.team-chat.spy.description");
        this.setOnlyPlayer(true);
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        Chat addon = this.getAddon();
        if (addon.getListener().toggleSpy(user.getUniqueId())) {
            user.sendMessage("chat.team-chat.spy.spy-on");
        } else {
            user.sendMessage("chat.team-chat.spy.spy-off");
        }
        return true;
    }
}