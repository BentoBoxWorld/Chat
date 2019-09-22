package world.bentobox.chat.commands.island;

import world.bentobox.bentobox.api.commands.CompositeCommand;
import world.bentobox.bentobox.api.user.User;
import world.bentobox.chat.Chat;

import java.util.List;
import java.util.Optional;

/**
 * @author Poslovitch
 */
public class IslandChatCommand extends CompositeCommand {

	public IslandChatCommand(Chat addon, CompositeCommand parent) {
		super(addon, parent, "chat");
	}

	@Override
	public void setup() {
		setConfigurableRankCommand();
	}

	@Override
	public boolean canExecute(User user, String label, List<String> args) {
		return false;
	}

	@Override
	public boolean execute(User user, String s, List<String> list) {
		return false;
	}

	@Override
	public Optional<List<String>> tabComplete(User user, String alias, List<String> args) {
		return Optional.empty();
	}
}
