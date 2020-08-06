package com.webcheckers.ui;

import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.model.Player;
import spark.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

import static com.webcheckers.util.Attributes.*;

/**
 * The UI Controller to GET the Sign In page
 *
 * @author <a href='mailto:np9379@rit.edu'>Nathan Page</a>
 * @author <a href='mailto:kkt7778@rit.edu'>Ketaki Tilak</a>
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 */
public class GetSigninRoute implements Route {
    private static final Logger LOG = getLogger(GetSigninRoute.class);

    private final TemplateEngine templateEngine;
    private final PlayerLobby playerLobby;

    /**
     * Create the Spark Route (UI controller) for the
     * {@code GET /} HTTP request.
     *
     * @param templateEngine the HTML template rendering engine
     */
    public GetSigninRoute(TemplateEngine templateEngine, PlayerLobby playerLobby) {
        Objects.requireNonNull(templateEngine, TEMPLATE_ENGINE_MSG);

        this.templateEngine = templateEngine;
        this.playerLobby = playerLobby;
        initialize(GetSigninRoute.class);
    }

    /**
     * Render the WebCheckers signin page
     *
     * @param request  the HTTP request
     * @param response the HTTP response
     * @return the rendered HTML for the Sign-in page
     */
    @Override
    public Object handle(Request request, Response response) {
        // This is run every time
        invoke(GetSigninRoute.class);
        Map<String, Object> vm = new HashMap<>();
        final Session session = request.session();

        if (session.attribute(CURRENT_PLAYER_ATTR) == null) {
            // If not signed in, send to sign in page

            vm.put(TITLE_ATTR, SIGN_IN_TITLE);
            if (session.attribute(PLAYER_MESSAGE_ATTR) != null) {
                // If player has message, display it
                vm.put(PLAYER_MESSAGE_ATTR, session.attribute(PLAYER_MESSAGE_ATTR).toString());
                session.removeAttribute(PLAYER_MESSAGE_ATTR);
            }

            return templateEngine.render(new ModelAndView(vm, SIGNIN_FTL));
        } else if (playerLobby.getOpponentOf((Player) session.attribute(CURRENT_PLAYER_ATTR)) != null) {
            // If player has been put in game
            session.attribute(OPPONENT_ATTR, playerLobby.getOpponentOf((Player) session.attribute(CURRENT_PLAYER_ATTR)));
            response.redirect(WebServer.GAME_URL);
        } else {
            // Player is signed in but not in game
            response.redirect(WebServer.HOME_URL);
        }

        return null;
    }
}
