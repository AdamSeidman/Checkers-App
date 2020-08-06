package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.Message;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import static com.webcheckers.util.Attributes.*;
import static java.lang.Boolean.TRUE;

/**
 * For spectator mode. Switch the sides of the view.
 *
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 */
public class PostSwitchSides implements Route {

    private final GameCenter gameCenter;
    private final Gson gson;

    public PostSwitchSides(final Gson gson, final GameCenter gameCenter) {
        this.gson = gson;
        this.gameCenter = gameCenter;

        initialize(PostSwitchSides.class);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        invoke(PostSwitchSides.class);

        Session httpSession = request.session();
        Message retMessage = new Message(TRUE.toString(), Message.Type.info);

        if (gameCenter.getGame(httpSession.attribute(GAME_ID_ATTR)) == null) {
            retMessage = new Message(NON_EXISTANT_GAME_MSG, Message.Type.error);
        }

        httpSession.attribute(FLIPPED_ATTR, !((boolean) httpSession.attribute(FLIPPED_ATTR)));

        return gson.toJson(retMessage);
    }
}
