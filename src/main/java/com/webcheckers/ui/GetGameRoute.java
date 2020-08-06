package com.webcheckers.ui;

import com.webcheckers.appl.ClientLobby;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.model.Game;
import com.webcheckers.model.Message;
import com.webcheckers.model.Piece;
import com.webcheckers.model.Player;
import spark.*;

import java.util.Map;
import java.util.Objects;

import static com.webcheckers.util.Attributes.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * The UI Controller to GET the Game page
 *
 * @author <a href="mailto:ajs1551@it.edu">Adam Seidman</a>
 * @author <a href='mailto:np9379@rit.edu'>Nathan Page</a>
 * @author <a href='mailto:kkt7778@rit.edu'>Ketaki Tilak</a>
 */
public class GetGameRoute implements Route {
    private final TemplateEngine templateEngine;
    private final PlayerLobby playerLobby;
    private final GameCenter gameCenter;
    private final ClientLobby clientLobby;

    public GetGameRoute(TemplateEngine templateEngine, PlayerLobby playerLobby, GameCenter gameCenter, ClientLobby clientLobby) {
        Objects.requireNonNull(templateEngine, TEMPLATE_ENGINE_MSG);

        this.templateEngine = templateEngine;
        this.playerLobby = playerLobby;
        this.gameCenter = gameCenter;
        this.clientLobby = clientLobby;

        initialize(GetGameRoute.class);
    }

    @Override
    public Object handle(Request request, Response response) {
        invoke(GetGameRoute.class);

        // HTTP Session
        Session httpSession = request.session();
        Player currentPlayer = httpSession.attribute(CURRENT_PLAYER_ATTR);

        if (currentPlayer != null) {
            clientLobby.updatePlayerTime(currentPlayer);
        }

        Game game;

        if (httpSession.attribute(SESSION_ID_ATTR) == null) {
            // If player has not yet seen this server, send them home
            response.redirect(WebServer.HOME_URL);
            return null;
        }

        try {
            if (httpSession.attribute(GAME_ID_ATTR) == null) {
                httpSession.attribute(GAME_ID_ATTR, playerLobby.getPlayerGameID(currentPlayer));
            }

            game = this.gameCenter.getGame(httpSession.attribute(GAME_ID_ATTR));
        } catch (Exception e) {
            // Player was not in game.
            httpSession.removeAttribute(GAME_ID_ATTR);
            response.redirect(WebServer.HOME_URL);
            return null;
        }

        if (game == null) {
            // Game has ended due to loss or resignation.
            boolean resigned = gameCenter.checkout(httpSession.attribute(GAME_ID_ATTR));
            httpSession.removeAttribute(GAME_ID_ATTR);
            httpSession.attribute(PLAYER_MESSAGE_ATTR, new Message(resigned ? PARTNER_RESIGNED_MSG : LOSE_MSG, Message.Type.info));
            response.redirect(resigned ? WebServer.HOME_URL : WebServer.GAMEEND_URL);
        } else if (game.isOver()) {
            // Game has ended due to the game being won.
            int gameID = httpSession.attribute(GAME_ID_ATTR);
            this.playerLobby.gameFinished(gameID);
            this.gameCenter.removeGame(gameID, FALSE);
            httpSession.removeAttribute(GAME_ID_ATTR);
            if ((game.getWinnerColor().equals(Piece.Color.RED) && currentPlayer.equals(game.getRedPlayer())) ||
                    (game.getWinnerColor().equals(Piece.Color.WHITE) && currentPlayer.equals(game.getWhitePlayer()))) {
                httpSession.attribute(PLAYER_MESSAGE_ATTR, WIN_MSG);
                response.redirect(WebServer.GAMEEND_URL); // Go to gameEnd.ftl
                return null;
            }
            response.redirect(WebServer.HOME_URL); // Bad Case
        } else {
            // Make game map
            Map<String, Object> vm = game.getGameAttributes(currentPlayer);
            game.reload();
            if (httpSession.attribute(FLIPPED_ATTR) != null) {
                // Is player is a spectator, flip the board if applicable
                vm = game.getGameAttributes(currentPlayer, httpSession.attribute(FLIPPED_ATTR));
            }
            vm.put(TITLE_ATTR, MAIN_TITLE);
            vm.put(CURRENT_PLAYER_ATTR, httpSession.attribute(CURRENT_PLAYER_ATTR));
            vm.put(AUTOSTART_ATTR, TRUE);

            return templateEngine.render(new ModelAndView(vm, GAME_FTL));
        }
        return null;
    }
}
