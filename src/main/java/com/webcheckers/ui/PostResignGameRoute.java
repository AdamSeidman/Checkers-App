package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.model.Message;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.logging.Logger;

import static com.webcheckers.util.Attributes.*;
import static java.lang.Boolean.TRUE;

/**
 * Handles POST \resignGame when user chooses to resign mid checkers game.
 *
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 */
public class PostResignGameRoute implements Route {
    private static final Logger LOG = getLogger(PostResignGameRoute.class);

    private final PlayerLobby playerLobby;
    private final Gson gson;
    private final GameCenter gameCenter;

    /**
     * Create the Spark Route (UI controller) for the
     * {@code POST /} HTTP request.
     *
     * @param playerLobby {@Link PlayerLobby}
     * @param gson        Gson to return message from
     * @param gameCenter  {@Link GameCenter} which the {@Link Game} is located.
     */
    public PostResignGameRoute(final PlayerLobby playerLobby, final Gson gson, final GameCenter gameCenter) {
        this.playerLobby = playerLobby;
        this.gson = gson;
        this.gameCenter = gameCenter;

        initialize(PostResignGameRoute.class);
    }

    /**
     * Render the WebCheckers Home page.
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return the rendered HTML for the Home page
     */
    @Override
    public Object handle(Request request, Response response) {
        invoke(PostResignGameRoute.class);
        try {
            final Session httpSession = request.session();
            int gameID = httpSession.attribute(GAME_ID_ATTR);
            this.playerLobby.gameFinished(gameID);
            this.gameCenter.removeGame(gameID, TRUE);
            httpSession.removeAttribute(GAME_ID_ATTR);
        } catch (Exception e) {
            return this.gson.toJson(new Message(QUIT_FAIL_MSG, Message.Type.error));
        }
        return this.gson.toJson(new Message(TRUE.toString(), Message.Type.info));
    }
}
