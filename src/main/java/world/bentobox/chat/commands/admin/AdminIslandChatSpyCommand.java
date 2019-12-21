package world.bentobox.chat.commands.admin;

import java.util.List;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.chat.Chat;

public class AdminIslandChatSpyCommand extends CompositeCommand {

    public AdminIslandChatSpyCommand(Chat chat, CompositeCommand parent, String label) {
        super(chat, parent, label);
    }

    @Override
    public void setup() {
        this.setPermission("chat.spy");
        this.setDescription("chat.island-chat.spy.description");
        this.setOnlyPlayer(true);
    }

    @Override
    public boolean execute(User user, String label, List<String> args) {
        Chat addon = this.getAddon();
        if (addon.getListener().toggleIslandSpy(user.getUniqueId())) {
            user.sendMessage("chat.island-chat.spy.on");
        } else {
            user.sendMessage("chat.island-chat.spy.off");
        }
        return true;
    }
}
