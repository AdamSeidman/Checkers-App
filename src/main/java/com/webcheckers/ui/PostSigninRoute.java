package com.webcheckers.ui;

import com.webcheckers.appl.ClientLobby;
import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.model.Message;
import com.webcheckers.model.Player;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.logging.Logger;

import static com.webcheckers.util.Attributes.*;


/**
 * The UI controller to POST to the Sign-In page
 *
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 * @author <a href='mailto:kkt7778@rit.edu'>Ketaki Tilak</a>
 */
public class PostSigninRoute implements Route {
    static final Logger LOG = getLogger(GetHomeRoute.class);

    private final PlayerLobby playerLobby;
    private final ClientLobby clientLobby;

    /**
     * The constructor for the {@code POST "<username>" [valid username]/home} route handler.
     *
     * @param playerLobby {@Link PlayerLobby}
     */
    public PostSigninRoute(final PlayerLobby playerLobby, final ClientLobby clientLobby) {
        this.playerLobby = playerLobby;
        this.clientLobby = clientLobby;

        initialize(PostSigninRoute.class);
    }

    /**
     * {@inheritDoc}
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    public String handle(Request request, Response response) {
        invoke(PostSigninRoute.class);

        final Session session = request.session();
        final String name = request.queryParams(NAME_ATTR);
        Player currentPlayer = new Player(name);
        long sessionID = 0;

        try {
            sessionID = session.attribute(SESSION_ID_ATTR);
        } catch (NullPointerException ignored) {
        }

        if (name.trim().length() < name.length()) {
            // Players name starts or ends with a space
            session.attribute(PLAYER_MESSAGE_ATTR, new Message(SPACES_NAME_MSG, Message.Type.error));
        } else if (!playerLobby.nameLengthCorrect(name)) {
            // Name too long
            session.attribute(PLAYER_MESSAGE_ATTR, new Message(NAME_LONG_MSG, Message.Type.error));
        } else if (playerLobby.nameExists(name)) {
            // Name already used
            session.attribute(PLAYER_MESSAGE_ATTR, new Message(PLAYER_NAME_USED_MSG, Message.Type.error));
        } else if (playerLobby.signIn(currentPlayer)) {
            // Player signs in with valid username
            session.attribute(CURRENT_PLAYER_ATTR, currentPlayer);
            if (sessionID != SESSION_ID) {
                // If client does not have session ID, give it to them.
                session.attribute(SESSION_ID_ATTR, SESSION_ID);
            }
            this.clientLobby.signIn(request.ip(), currentPlayer.getName());
            response.redirect(WebServer.HOME_URL);
            return null;
        } else {
            // Players username is not valid
            session.attribute(PLAYER_MESSAGE_ATTR, new Message(INVALID_USERNAME_MSG, Message.Type.error));
        }

        response.redirect(WebServer.SIGNIN_URL);
        return null;
    }
}
