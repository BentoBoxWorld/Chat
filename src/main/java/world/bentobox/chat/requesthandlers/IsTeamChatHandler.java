package world.bentobox.chat.requesthandlers;

import java.util.Map;
import java.util.UUID;

import world.bentobox.bentobox.api.addons.request.AddonRequestHandler;
import world.bentobox.chat.Chat;

/**
 * Handles API requests from plugins
 * @author tastybento
 *
 */
public class IsTeamChatHandler extends AddonRequestHandler {

    private Chat addon;

    /**
     * @param label
     */
    public IsTeamChatHandler(Chat addon) {
        super("isTeamChat");
        this.addon = addon;
    }

    /* (non-Javadoc)
     * @see world.bentobox.bentobox.api.addons.request.AddonRequestHandler#handle(java.util.Map)
     */
    @Override
    public Object handle(Map<String, Object> map) {
        /*
        What we need in the map:
        1. "uuid" -> UUID of player to check
        What we will return:
        boolean
        - true if player is in team chat
        - false if not in team chat
         */
        // Error checking
        if (map == null || map.isEmpty()
                || map.get("uuid") == null || !(map.get("uuid") instanceof UUID)) {
            return false;
        }

        return addon.getListener().isTeamChat((UUID) map.get("uuid"));
    }

}
