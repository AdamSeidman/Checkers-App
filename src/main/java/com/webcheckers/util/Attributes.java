package com.webcheckers.util;

import com.webcheckers.model.Player;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import static java.lang.Boolean.TRUE;

/**
 * Attributes used in WebCheckers
 *
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 */
public abstract class Attributes {

    // ================ TITLES ================
    public static final String MAIN_TITLE = "Web Checkers";
    public static final String SIGN_IN_TITLE = "Sign In";
    public static final String WELCOME_TITLE = "Welcome!";

    // ================ CONSTANTS ================
    public static final int CHECKERBOARD_ROWS = 8;
    public static final int CHECKERBOARD_SPACES = 8;
    public static final int MAX_NAME_LENGTH = 32;
    public static boolean STORE_CLIENTS = TRUE;
    public static long SESSION_ID = new Random().nextLong();
    public static final long EXPIRE_TIME = 300000;
    public static final String EXPIRE_CHAR = "!";

    // ================ MESSAGES ================
    public static final String GSON_NULL_MSG = "gson must not be null";
    public static final String TEMPLATE_ENGINE_MSG = "templateEngine must not be null.";
    public static final String MESSAGE_ILLEGAL_ARGUMENT_EXCEPTION_MSG = "Valid Message Type required.";
    public static final String SIGN_OUT_MSG = "You have been successfully signed out!";
    public static final String INVALID_USERNAME_MSG = "Username Invalid! Pick Again. Please use [a-z]/[A-Z]/[0-9]/spaces only.";
    public static final String PLAYER_CREATED_MSG = "Player Created ";
    public static final String WEB_SERVER_INIT_MSG = "WebServer is initialized.";
    public static final String SIGN_OUT_LOG_MSG = "Player Removed ";
    public static final String WRONG_TURN_MSG = "Waiting for turn...";
    public static final String MOVES_MADE_MSG = "Turn Submitted!";
    public static final String NO_MOVES_MSG = "No moves were made.";
    public static final String GOOD_MOVE_MSG = "Nice move, champ!";
    public static final String BAD_MOVE_MSG = "That is not a valid move.";
    public static final String TOO_MANY_MOVES_MSG = "You cannot move any more spaces.";
    public static final String CANT_UNDO_MSG = "There are no moves to undo!";
    public static final String UNDO_SUCCESS_MSG = "Move Undo Successful!";
    public static final String PARTNER_RESIGNED_MSG = "Your partner resigned. You have won!";
    public static final String QUIT_FAIL_MSG = "Failure. Partner Resigned. Make move to end game.";
    public static final String WIN_MSG = "YOU HAVE WON THE GAME!! Play Again?";
    public static final String LOSE_MSG = "Sorry, but you have lost the game. Play Again?";
    public static final String SPACES_NAME_MSG = "Sorry, but you cannot use a name that starts or ends with a space. Please try again.";
    public static final String MID_GAME_PARTNER_RESIGN_MSG = "Your partner resigned mid-game. Please press submit to end the game.";
    public static final String JUMP_MOVE_AVAILABLE_MSG = "There is a jump move available. You must take it!";
    public static final String ALREADY_JUMPED_MSG = "This piece has already been jumped.";
    public static final String PLAYER_NAME_USED_MSG = "Sorry, but that player name is already taken. Please try again.";
    public static final String NAME_LONG_MSG = "Sorry, but that name is too long. The max length is " + MAX_NAME_LENGTH + " characters.";
    public static final String NON_EXISTANT_GAME_MSG = "This game no longer exists.";
    public static final String SPECTATE_GAME_ENDED_MSG = "The game you were spectating is over.";

    // ================ ATTRIBUTES ================
    public static final String SIGNED_IN_ATTR = "playerSignedIn";
    public static final String TITLE_ATTR = "title";
    public static final String CURRENT_PLAYER_ATTR = "currentPlayer";
    public static final String OPPONENT_ATTR = "opponent";
    public static final String PLAYER_MESSAGE_ATTR = "playerMessage";
    public static final String BOARD_ATTR = "board";
    public static final String RED_PLAYER_ATTR = "redPlayer";
    public static final String WHITE_PLAYER_ATTR = "whitePlayer";
    public static final String VIEW_MODE_ATTR = "viewMode";
    public static final String AUTOSTART_ATTR = "autostart";
    public static final String ACTIVE_COLOR_ATTR = "activeColor";
    public static final String PLAYER_LIST_ATTR = "playerList";
    public static final String NAME_ATTR = "name";
    public static final String GAME_ID_ATTR = "gameID";
    public static final String FLIPPED_ATTR = "isFlipped";
    public static final String SESSION_ID_ATTR = "sessionID";
    public static final String EXPIRE_TEXT_ATTR = "expire_text";

    public static final List<String> ATTRIBUTES = new ArrayList();

    static {
        for (Field field : Attributes.class.getFields()) {
            if (field.getName().contains("_ATTR")) {
                try {
                    ATTRIBUTES.add((String) field.get(""));
                } catch (IllegalAccessException ignored) {
                    // won't happen
                }
            }
        }
    }

    // ================ ADDRESSES ================
    public static final String HOME_FTL = "home.ftl";
    public static final String GAME_FTL = "game.ftl";
    public static final String SIGNIN_FTL = "signin.ftl";
    public static final String GAME_END_FTL = "gameEnd.ftl";
    public static final String STATIC_FILE_LOCATION = "/public";
    public static final String CLIENT_LOG_FILE_PATH = "clients.dat";
    public static final String GAME_LOG_FILE_PATH = "games.dat";
    public static final String LOBBY_LOG_FILE_PATH = "lobby.dat";

    // ================ FORMATTERS ================

    public static Logger getLogger(Class class_) {
        return Logger.getLogger(class_.getName());
    }

    /**
     * Log a [class_name] is invoked. message
     *
     * @param class_ The class being invoked
     */
    public static void invoke(Class class_) {
        Attributes.getLogger(class_).finer(String.format("%s is invoked.", class_.getName()));
    }

    /**
     * Log a [class_name] is initialized message
     *
     * @param class_ The class being initialized
     */
    public static void initialize(Class class_) {
        Attributes.getLogger(class_).config(String.format("%s is initialized", class_.getName()));
    }

    /**
     * Get message to log when starting game message between two players
     *
     * @param player1 {@Link Player} players in-game
     * @param player2 {@Link Player}
     * @return The {@Linkplain String} to log.
     */
    public static String gameStartMsg(Player player1, Player player2) {
        return String.format("Game Starting Between %s and %s", player1.getName(), player2.getName());
    }
}
