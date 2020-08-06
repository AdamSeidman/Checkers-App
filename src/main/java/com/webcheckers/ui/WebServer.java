package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.appl.ClientLobby;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerLobby;
import spark.TemplateEngine;

import java.util.Objects;
import java.util.logging.Logger;

import static com.webcheckers.util.Attributes.*;
import static spark.Spark.*;


/**
 * The server that initializes the set of HTTP request handlers.
 * This defines the <em>web application interface</em> for this
 * WebCheckers application.
 * <p>
 * <p>
 * There are multiple ways in which you can have the client issue a
 * request and the application generate responses to requests. If your team is
 * not careful when designing your approach, you can quickly create a mess
 * where no one can remember how a particular request is issued or the response
 * gets generated. Aim for consistency in your approach for similar
 * activities or requests.
 * </p>
 * <p>
 * <p>Design choices for how the client makes a request include:
 * <ul>
 * <li>Request URL</li>
 * <li>HTTP verb for request (GET, POST, PUT, DELETE and so on)</li>
 * <li><em>Optional:</em> Inclusion of request parameters</li>
 * </ul>
 * </p>
 * <p>
 * <p>Design choices for generating a response to a request include:
 * <ul>
 * <li>View templates with conditional elements</li>
 * <li>Use different view templates based on results of executing the client request</li>
 * <li>Redirecting to a different application URL</li>
 * </ul>
 * </p>
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 * @author <a href='mailto:ajs1551@rit.edu'>Adam Seidman</a>
 * @author <a href='mailto:kkt7778@rit.edu'>Ketaki Tilak</a>
 */
public class WebServer {

    private static final Logger LOG = Logger.getLogger(WebServer.class.getName());

    /**
     * The URL pattern to request the Home page.
     */
    public static final String HOME_URL = "/";
    public static final String SIGNIN_URL = "/signin";
    public static final String GAME_URL = "/game";
    public static final String STARTGAME_URL = "/startGame";
    public static final String RESIGNGAME_URL = "/resignGame";
    public static final String SIGNOUT_URL = "/signout";
    public static final String VALIDATEMOVE_URL = "/validateMove";
    public static final String SUBMITTURN_URL = "/submitTurn";
    public static final String CHECKTURN_URL = "/checkTurn";
    public static final String BACKUPMOVE_URL = "/backupMove";
    public static final String GAMEEND_URL = "/gameEnd";
    public static final String SWITCHSIDES_URL = "/switchSides";
    public static final String ENDSPECTATE_URL = "/endSpectate";

    private final TemplateEngine templateEngine;
    private final PlayerLobby playerLobby;
    private final Gson gson;
    private final GameCenter gameCenter;
    private final ClientLobby clientLobby;

    /**
     * The constructor for the Web Server.
     *
     * @param templateEngine The default {@link TemplateEngine} to render page-level HTML views.
     * @param gson           The Google JSON parser object used to render Ajax responses.
     * @throws NullPointerException If any of the parameters are {@code null}.
     */
    public WebServer(final PlayerLobby playerLobby, final TemplateEngine templateEngine,
                     final Gson gson, final GameCenter gameCenter, final ClientLobby clientLobby) {
        Objects.requireNonNull(templateEngine, TEMPLATE_ENGINE_MSG);
        Objects.requireNonNull(gson, GSON_NULL_MSG);

        this.playerLobby = playerLobby;
        this.templateEngine = templateEngine;
        this.gson = gson;
        this.gameCenter = gameCenter;
        this.clientLobby = clientLobby;
    }

    /**
     * Initialize all of the HTTP routes that make up this web application.
     * <p>
     * <p>
     * Initialization of the web server includes defining the location for static
     * files, and defining all routes for processing client requests. The method
     * returns after the web server finishes its initialization.
     * </p>
     */
    public void initialize() {

        // Configuration to serve static files
        staticFileLocation(STATIC_FILE_LOCATION);

        // Shows the Checkers game Home page.
        get(HOME_URL, new GetHomeRoute(templateEngine, playerLobby, clientLobby));

        get(SIGNIN_URL, new GetSigninRoute(templateEngine, playerLobby));

        get(GAME_URL, new GetGameRoute(templateEngine, playerLobby, gameCenter, clientLobby));

        get(GAMEEND_URL, new GetGameEndRoute(templateEngine, playerLobby));

        get(SIGNOUT_URL, new GetSignOutRoute(playerLobby, gameCenter, clientLobby));

        post(CHECKTURN_URL, new PostCheckTurnRoute(gson, gameCenter, playerLobby, clientLobby));

        post(SIGNIN_URL, new PostSigninRoute(playerLobby, clientLobby));

        post(STARTGAME_URL, new PostStartGameRoute(playerLobby, gameCenter));

        post(VALIDATEMOVE_URL, new PostValidateMoveRoute(gson, gameCenter));

        post(RESIGNGAME_URL, new PostResignGameRoute(playerLobby, gson, gameCenter));

        post(SUBMITTURN_URL, new PostSubmitTurnRoute(gson, gameCenter));

        post(BACKUPMOVE_URL, new PostBackupMoveRoute(gson, gameCenter));

        post(SWITCHSIDES_URL, new PostSwitchSides(gson, gameCenter));

        post(ENDSPECTATE_URL, new PostEndSpectateRoute(gson, playerLobby));

        LOG.config(WEB_SERVER_INIT_MSG);
    }

}