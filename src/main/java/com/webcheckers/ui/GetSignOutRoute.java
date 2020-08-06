package com.webcheckers.ui;

import com.webcheckers.appl.ClientLobby;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Message;
import com.webcheckers.model.Player;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.logging.Logger;

import static com.webcheckers.util.Attributes.*;
import static java.lang.Boolean.TRUE;

/**
 * Handles POST \signOutGame when user chooses to sign-out.
 *
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 * @author <a href="mailto:kkt7778@rit.edu">Ketaki Tilak</a>
 */
public class GetSignOutRoute implements Route {

    private static final Logger LOG = getLogger(PostResignGameRoute.class);

    private final PlayerLobby playerLobby;
    private final GameCenter gameCenter;
    private final ClientLobby clientLobby;

    /**
     * Create the Spark Route (UI controller) for the
     * {@code POST /} HTTP request.
     *
     * @param playerLobby the playerLobby where the players are stored.
     */
    public GetSignOutRoute(final PlayerLobby playerLobby, final GameCenter gameCenter, final ClientLobby clientLobby) {
        this.playerLobby = playerLobby;
        this.clientLobby = clientLobby;
        this.gameCenter = gameCenter;

        initialize(GetSignOutRoute.class);
    }

    /**
     * Signs current player out
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return the rendered HTML for the Home page
     */
    @Override
    public Object handle(Request request, Response response) {
        invoke(GetSignOutRoute.class);
        Session httpSession = request.session();
        Player currentPlayer = httpSession.attribute(CURRENT_PLAYER_ATTR);

        try {
            int gameID = httpSession.attribute(GAME_ID_ATTR);
            Game game = this.gameCenter.getGame(gameID);
            if (game.getWhitePlayer().equals(currentPlayer) || game.getRedPlayer().equals(currentPlayer)) {
                // If player signing out is not a spectator, don't end the game
                this.playerLobby.gameFinished(gameID);
                this.gameCenter.removeGame(gameID, TRUE);
            }
        } catch (NullPointerException e) {
            // When player not in game- is natural
        }

        // Sign out player
        this.playerLobby.signOut(currentPlayer);
        this.clientLobby.signOut(request.ip());

        LOG.fine(SIGN_OUT_LOG_MSG + currentPlayer.getName());

        // Send Player back to '/home' and display sign out message
        for (String attr : httpSession.attributes()) {
            httpSession.removeAttribute(attr);
        }
        httpSession.attribute(PLAYER_MESSAGE_ATTR, new Message(SIGN_OUT_MSG, Message.Type.info));
        response.redirect(WebServer.HOME_URL);
        return null;
    }
}
