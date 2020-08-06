package com.webcheckers.ui;

import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.model.Player;
import spark.*;

import java.util.HashMap;
import java.util.Map;

import static com.webcheckers.util.Attributes.*;

/**
 * Displays end of game page.
 *
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 */
public class GetGameEndRoute implements Route {

    private final TemplateEngine templatEngine;
    private final PlayerLobby playerLobby;

    public GetGameEndRoute(TemplateEngine templateEngine, PlayerLobby playerLobby) {
        this.templatEngine = templateEngine;
        this.playerLobby = playerLobby;

        initialize(GetGameEndRoute.class);
    }

    @Override
    public Object handle(Request request, Response response) {
        invoke(GetGameEndRoute.class);
        Map<String, Object> vm = new HashMap<>();
        Session httpSession = request.session();
        vm.put(TITLE_ATTR, MAIN_TITLE);
        vm.put(SIGNED_IN_ATTR, httpSession.attribute(CURRENT_PLAYER_ATTR) != null);
        vm.put(CURRENT_PLAYER_ATTR, httpSession.attribute(CURRENT_PLAYER_ATTR));
        if (httpSession.attribute(PLAYER_MESSAGE_ATTR) != null) {
            // If a player message exists, add it and remove it.
            vm.put(PLAYER_MESSAGE_ATTR, httpSession.attribute(PLAYER_MESSAGE_ATTR));
            httpSession.removeAttribute(PLAYER_MESSAGE_ATTR);
        }
        try {
            Player currentPlayer = httpSession.attribute(CURRENT_PLAYER_ATTR);
            if (this.playerLobby.isSpectator(currentPlayer.getName())) {
                // Mark spectators as not spectating and show correct message
                this.playerLobby.removeSpectator(currentPlayer);
                vm.put(PLAYER_MESSAGE_ATTR, SPECTATE_GAME_ENDED_MSG);
            }
        } catch (Exception ignored) {
            // Happens with non-spectator players
        }
        return templatEngine.render(new ModelAndView(vm, GAME_END_FTL));
    }
}
