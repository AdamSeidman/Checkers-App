package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.Game;
import com.webcheckers.model.Message;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import static com.webcheckers.util.Attributes.*;

/**
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 * @author <a href='mailto:np9379@rit.edu'>Nathan Page</a>
 */

public class PostBackupMoveRoute implements Route {
    private final Gson gson;
    private final GameCenter gameCenter;

    public PostBackupMoveRoute(Gson gson, GameCenter gameCenter) {
        this.gson = gson;
        this.gameCenter = gameCenter;

        initialize(PostBackupMoveRoute.class);
    }

    @Override
    public Object handle(Request request, Response response) {
        invoke(PostBackupMoveRoute.class);

        Session httpSession = request.session();
        Game game = this.gameCenter.getGame(httpSession.attribute(GAME_ID_ATTR));

        if (game == null) {
            return this.gson.toJson(new Message(MID_GAME_PARTNER_RESIGN_MSG, Message.Type.info));
        } else if (game.getInitialPosition(null) == null) {
            return this.gson.toJson(new Message(CANT_UNDO_MSG, Message.Type.error));
        } else {
            game.backUp();
            return this.gson.toJson(new Message(UNDO_SUCCESS_MSG, Message.Type.info));
        }
    }
}
