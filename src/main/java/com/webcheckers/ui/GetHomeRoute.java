package com.webcheckers.ui;

import com.webcheckers.appl.ClientLobby;
import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.model.Player;
import spark.*;

import java.util.*;
import java.util.logging.Logger;

import static com.webcheckers.util.Attributes.*;
import static java.lang.Boolean.TRUE;

/**
 * The UI Controller to GET the Home page.
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 * @author <a href='mailto:np9379@rit.edu'>Nathan Page</a>
 * @author <a href='mailto:ajs1551@rit.edu'>Adam Seidman</a>
 */
public class GetHomeRoute implements Route {
    private static final Logger LOG = getLogger(GetHomeRoute.class);

    private final TemplateEngine templateEngine;
    private final PlayerLobby playerLobby;
    private final ClientLobby clientLobby;

    /**
     * Create the Spark Route (UI controller) for the
     * {@code GET /} HTTP request.
     *
     * @param templateEngine the HTML template rendering engine
     */
    public GetHomeRoute(final TemplateEngine templateEngine, final PlayerLobby playerLobby, final ClientLobby clientLobby) {
        Objects.requireNonNull(templateEngine, TEMPLATE_ENGINE_MSG);

        this.templateEngine = templateEngine;
        this.playerLobby = playerLobby;
        this.clientLobby = clientLobby;

        initialize(GetHomeRoute.class);
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
        invoke(GetHomeRoute.class);

        final Session httpSession = request.session(); // Get the currentPlayer
        Player currentPlayer = httpSession.attribute(CURRENT_PLAYER_ATTR);
        long sessionID = 0;

        try {
            if (currentPlayer != null) {
                // Update times
                this.clientLobby.updatePlayerTime(currentPlayer);
            }
            sessionID = httpSession.attribute(SESSION_ID_ATTR);
        } catch (NullPointerException ignored) {
        }

        if (sessionID != SESSION_ID) {
            for (String attr : httpSession.attributes()) {
                // Remove all attributes.
                httpSession.removeAttribute(attr);
            }
            if (clientLobby.has(request.ip())) {
                // Client already is logged in and is being put back in game
                currentPlayer = clientLobby.get(request.ip());
                clientLobby.updatePlayerTime(currentPlayer);
                httpSession.attribute(CURRENT_PLAYER_ATTR, currentPlayer);
                httpSession.attribute(SIGNED_IN_ATTR, TRUE);
                if (playerLobby.getOpponentOf(currentPlayer.getName()) != null) {
                    // If player is in a game, give them their gameID attribute
                    httpSession.attribute(GAME_ID_ATTR, playerLobby.getPlayerGameID(currentPlayer));
                }
            }
            // Give client the session ID.
            httpSession.attribute(SESSION_ID_ATTR, SESSION_ID);
        }

        try {
            if (playerLobby.getOpponentOf(currentPlayer.getName()) != null) {
                // Player needs to be in game and is not
                response.redirect(WebServer.GAME_URL);
                return null;
            }
        } catch (NullPointerException ignored) {
        }

        Map<String, Object> vm = new HashMap<>();
        vm.put(TITLE_ATTR, WELCOME_TITLE); // Map home page
        vm.put(CURRENT_PLAYER_ATTR, currentPlayer);
        vm.put(SIGNED_IN_ATTR, httpSession.attribute(CURRENT_PLAYER_ATTR) != null); // Player is signed in if their player is not null

        Map<String, String> playerIDs = new HashMap<>();
        for (String name : playerLobby.getUserNamesList()) {
            // Give each player an identifier for home page css
            if (this.playerLobby.isSpectator(name)) {
                playerIDs.put(name, "IS_SPECTATOR");
            } else if (this.playerLobby.getOpponentOf(name) != null) {
                playerIDs.put(name, "IN_GAME");
            } else {
                playerIDs.put(name, "IS_OTHER");
            }
        }
        List<Map.Entry<String, String>> convertedMap = new ArrayList(playerIDs.entrySet());
        vm.put(PLAYER_LIST_ATTR, convertedMap);
        StringBuilder str = new StringBuilder(EXPIRE_CHAR);
        for (String name : clientLobby.getExpiredPlayers()) {
            str.append(name + EXPIRE_CHAR);
        }
        vm.put(EXPIRE_TEXT_ATTR, str.toString());

        if (httpSession.attribute(PLAYER_MESSAGE_ATTR) != null) { // Player has a message, display it
            vm.put(PLAYER_MESSAGE_ATTR, httpSession.attribute(PLAYER_MESSAGE_ATTR));
            httpSession.removeAttribute(PLAYER_MESSAGE_ATTR);
        }

        return templateEngine.render(new ModelAndView(vm, HOME_FTL));
    }

}