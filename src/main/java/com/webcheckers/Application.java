package com.webcheckers;

import com.google.gson.Gson;
import com.webcheckers.appl.ClientLobby;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.appl.PlayerLobby;
import com.webcheckers.ui.WebServer;
import spark.TemplateEngine;
import spark.template.freemarker.FreeMarkerEngine;

import java.io.InputStream;
import java.util.Objects;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import static com.webcheckers.util.Attributes.*;

/**
 * The entry point for the WebCheckers web application.
 *
 * @author <a href='mailto:bdbvse@rit.edu'>Bryan Basham</a>
 * @author <a href='mailto:ajs1551@rit.edu'>Adam Seidman</a>
 */
public final class Application {
    private static final Logger LOG = Logger.getLogger(Application.class.getName());

    //
    // Application Launch method
    //

    /**
     * Entry point for the WebCheckers web application.
     * <p>
     * <p>
     * It wires the application components together.  This is an example
     * of <a href='https://en.wikipedia.org/wiki/Dependency_injection'>Dependency Injection</a>
     * </p>
     *
     * @param args Command line arguments; none expected.
     */
    public static void main(String[] args) {
        // initialize Logging
        try {
            ClassLoader classLoader = Application.class.getClassLoader();
            final InputStream logConfig = classLoader.getResourceAsStream("log.properties");
            LogManager.getLogManager().readConfiguration(logConfig);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Could not initialize log manager because: " + e.getMessage());
        }

        // The application uses FreeMarker templates to generate the HTML
        // responses sent back to the client. This will be the engine processing
        // the templates and associated data.
        final TemplateEngine templateEngine = new FreeMarkerEngine();

        // The application uses Gson to generate JSON representations of Java objects.
        // This should be used by your Ajax Routes to generate JSON for the HTTP
        // response to Ajax requests.
        final Gson gson = new Gson();

        // game center where all game data is stored
        final GameCenter gameCenter = new GameCenter();


        // client lobby where all client information is stored
        final ClientLobby clientLobby = new ClientLobby(STORE_CLIENTS ? CLIENT_LOG_FILE_PATH : null);

        // player lobby where all player data is stored
        final PlayerLobby playerLobby = new PlayerLobby(gameCenter);

        // inject the game center and freemarker engine into web server
        final WebServer webServer = new WebServer(playerLobby, templateEngine, gson, gameCenter, clientLobby);

        // inject web server into application
        final Application app = new Application(webServer);

        // start the application up
        app.initialize();
    }

    private final WebServer webServer;

    private Application(final WebServer webServer) {
        Objects.requireNonNull(webServer, "webServer must not be null");

        this.webServer = webServer;
    }

    private void initialize() {
        LOG.config("WebCheckers is initializing.");

        // configure Spark and startup the Jetty web server
        webServer.initialize();

        // other applications might have additional services to configure

        LOG.config("WebCheckers initialization complete.");
    }

}