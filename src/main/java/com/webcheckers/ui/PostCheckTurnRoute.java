package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.appl.ClientLobby;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Message;
import com.webcheckers.model.Piece;
import com.webcheckers.model.Player;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import static com.webcheckers.util.Attributes.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Check turn on game page reload.
 *
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 */
public class PostCheckTurnRoute implements Route {

    private final Gson gson;
    private final GameCenter gameCenter;
    private final PlayerLobby playerLobby;
    private final ClientLobby clientLobby;

    public PostCheckTurnRoute(Gson gson, GameCenter gameCenter, PlayerLobby playerLobby, ClientLobby clientLobby) {
        this.gson = gson;
        this.gameCenter = gameCenter;
        this.playerLobby = playerLobby;
        this.clientLobby = clientLobby;

        initialize(PostCheckTurnRoute.class);
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        invoke(PostCheckTurnRoute.class);

        Session httpSession = request.session();
        boolean isRed;
        Piece.Color activeColor = Piece.Color.RED;

        try {
            clientLobby.updatePlayerTime((Player) httpSession.attribute(CURRENT_PLAYER_ATTR));
        } catch (Exception ignored) {
            // Player loaded manually - no need to log, will get redirected
        }

        if (gameCenter.getGame(httpSession.attribute(GAME_ID_ATTR)) == null) {
            // If opponent has resigned, render game view so that current player returns home.
            return gson.toJson(new Message(TRUE.toString(), Message.Type.info));
        }

        // Session objects on player color.
        try {
            Player currentPlayer = httpSession.attribute(CURRENT_PLAYER_ATTR);
            Game game = gameCenter.getGame(httpSession.attribute(GAME_ID_ATTR));

            if (game != null) {
                activeColor = game.getColor();
            }

            if (playerLobby.isSpectator(currentPlayer.getName())) {
                // Spectator is checking turn
                isRed = gameCenter.getGame(httpSession.attribute(GAME_ID_ATTR)).getColor().equals(Piece.Color.WHITE);
                if (game.moveMade()) {
                    // Reload page
                    return gson.toJson(new Message(TRUE.toString(), Message.Type.info));
                }
            } else {
                httpSession.attribute(PLAYER_MESSAGE_ATTR, new Message(LOSE_MSG, Message.Type.info));
                isRed = game.getRedPlayer().equals(currentPlayer);
            }
        } catch (NullPointerException e) {
            isRed = TRUE;
        }

        if ((isRed && activeColor == Piece.Color.RED) || (!isRed && activeColor == Piece.Color.WHITE)) {
            // If the active color if your color, this turn is okay
            return gson.toJson(new Message(TRUE.toString(), Message.Type.info));
        }

        return gson.toJson(new Message(WRONG_TURN_MSG, Message.Type.error)); // Turn not okay
    }
}
