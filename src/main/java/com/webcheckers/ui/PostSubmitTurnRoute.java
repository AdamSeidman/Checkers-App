package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.Game;
import com.webcheckers.model.Message;
import spark.Request;
import spark.Response;
import spark.Route;

import static com.webcheckers.util.Attributes.*;
import static java.lang.Boolean.TRUE;

/**
 * Submit moves to a board from a game.
 * Occurs on button click.
 *
 * @author <a href='mailto:ajs1551@rit.edu'>Adam Seidman</a>
 * @author <a href='mailto:np9379@rit.edu'>Nathan Page</a>
 */
public class PostSubmitTurnRoute implements Route {
    private Gson gson;

    private final GameCenter gameCenter;

    public PostSubmitTurnRoute(Gson gson, GameCenter gameCenter) {
        this.gson = gson;
        this.gameCenter = gameCenter;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        Game game = this.gameCenter.getGame(request.session().attribute(GAME_ID_ATTR));
        boolean movesMade = TRUE;

        if (game != null) {
            movesMade = game.submitMoves();
            this.gameCenter.log();
            if (movesMade) {
                // Switch the players turns if applicable.
                game.changePlayer();
            }
        }
        return gson.toJson(new Message(movesMade ? MOVES_MADE_MSG : NO_MOVES_MSG, movesMade ? Message.Type.info
                : Message.Type.error));
    }
}
