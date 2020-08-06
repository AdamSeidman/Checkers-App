package com.webcheckers.ui;

import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Player;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.Random;
import java.util.logging.Logger;

import static com.webcheckers.util.Attributes.*;

/**
 * When a player clicks on another {@Link Player}'s name, they go through this POST route.
 * They are redirected to \game .
 *
 * @author <a href='mailto:kkt7778@rit.edu'>Ketaki Tilak</a>
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 * @author <a href="mailto:agr3714@rit.edu">Audrey Rovero</a>
 * @author <a href="mailto:np9379@rit.edu">Nathan Page</a>
 */
public class PostStartGameRoute implements Route {
    private static Random random = new Random();
    private static final Logger LOG = getLogger(PostStartGameRoute.class);

    private final PlayerLobby playerLobby;
    private final GameCenter gameCenter;


    /**
     * PostHome web route for {@Link WebServer}
     *
     * @param playerLobby The {@Link PlayerLobby} where all of the {@Link Player}'s are stored
     */
    public PostStartGameRoute(PlayerLobby playerLobby, GameCenter gameCenter) {
        this.playerLobby = playerLobby;
        this.gameCenter = gameCenter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object handle(Request request, Response response) {
        invoke(PostStartGameRoute.class);

        Session httpSession = request.session();

        // Set up Players that will be playing the CheckersGame
        Player currentPlayer = httpSession.attribute(CURRENT_PLAYER_ATTR);
        Player opponent = this.playerLobby.getPlayer(request.queryParams(OPPONENT_ATTR));

        if (!playerLobby.isSpectator(opponent.getName()) && playerLobby.getOpponentOf(opponent) == null) {
            // Opponent is not yet in game
            Game game = new Game(currentPlayer, opponent);

            // Mark the players in the game
            int id;
            do {
                id = random.nextInt();
            } while (!this.gameCenter.addGame(id, game));
            this.playerLobby.markPlayersInGame(id);
            httpSession.attribute(GAME_ID_ATTR, id);

            LOG.fine(gameStartMsg(currentPlayer, opponent));
            response.redirect(WebServer.GAME_URL);
        }
        else if (playerLobby.getOpponentOf(opponent).equals(currentPlayer)) {
            // Player clicked start right after opponent did
            response.redirect(WebServer.GAME_URL);
        } else {
            // Opponent is already in game - start spectating
            int id;
            if (playerLobby.isSpectator(opponent.getName())) {
                // Opponent is spectator
                id = playerLobby.getGameSpectating(opponent.getName());
                httpSession.attribute(FLIPPED_ATTR, gameCenter.getGame(id).isActivePlayer(gameCenter.getGame(id).getWhitePlayer()));
            } else {
                // Originally picked person in game to spectate.
                id = playerLobby.getPlayerGameID(opponent);
                httpSession.attribute(FLIPPED_ATTR, !gameCenter.getGame(id).getRedPlayer().equals(opponent));
            }
            this.playerLobby.markSpectator(currentPlayer, id);
            httpSession.attribute(GAME_ID_ATTR, id);

            response.redirect(WebServer.GAME_URL);
        }

        return null;
    }
}
