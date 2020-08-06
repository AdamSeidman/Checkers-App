package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.model.Message;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import static com.webcheckers.util.Attributes.*;
import static java.lang.Boolean.TRUE;

/**
 * POST ajax route for leaving spectate mode.
 *
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 */
public class PostEndSpectateRoute implements Route {

    private final Gson gson;
    private final PlayerLobby playerLobby;

    public PostEndSpectateRoute(final Gson gson, final PlayerLobby playerLobby) {
        this.gson = gson;
        this.playerLobby = playerLobby;

        initialize(PostEndSpectateRoute.class);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        invoke(PostEndSpectateRoute.class);

        Session httpSession = request.session();
        httpSession.removeAttribute(FLIPPED_ATTR);
        this.playerLobby.removeSpectator(httpSession.attribute(CURRENT_PLAYER_ATTR));
        httpSession.removeAttribute(GAME_ID_ATTR);

        return gson.toJson(new Message(TRUE.toString(), Message.Type.info));
    }
}
