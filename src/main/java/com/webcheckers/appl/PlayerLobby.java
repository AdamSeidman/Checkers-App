package com.webcheckers.appl;

import com.webcheckers.model.Player;

import java.io.*;
import java.util.Collection;
import java.util.HashMap;

import static com.webcheckers.util.Attributes.*;

/**
 * Handle {@Link Player} sign-ins and sign-outs and handles player names.
 *
 * @author <a href='mailto:ajs1551@rit.edu'>Adam Seidman</a>
 */
public class PlayerLobby {

    private final GameCenter gameCenter;

    // Map of all players on the checkers WebApp server.
    private HashMap<Player, String> players;

    // Map to mark inactive players
    private HashMap<Player, Integer> gameMap;

    // Map for spectators
    private HashMap<String, Integer> spectatorMap = new HashMap<>();

    public PlayerLobby(final GameCenter gameCenter) {
        this.gameCenter = gameCenter;
        File readFile = new File(LOBBY_LOG_FILE_PATH);
        try (
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(readFile))
        ) {
            if (!STORE_CLIENTS) {
                // Create new collections rather than reading
                throw new IOException();
            }
            Object[] collections = (Object[]) ois.readObject();
            this.players = (HashMap<Player, String>) collections[0];
            this.gameMap = (HashMap<Player, Integer>) collections[1];
        } catch (IOException | ClassNotFoundException e) {
            // Make sure new collections exist
            if(readFile.exists()) {
                readFile.delete();
            }
            this.players = new HashMap<>();
            this.gameMap = new HashMap<>();
        }
    }

    /**
     * Handle Sign-in of player to WebApp.
     *
     * @param player {@Link Player} that is attempting to sign-in.
     * @return false if name is already being used.
     */
    public boolean signIn(Player player) {
        String name = player.getName();
        int length = name.length();
        char c;
        for (int i = 0; i < length; i++) {
            // Check all characters for being letters or numbers.
            c = name.charAt(i);
            if (!(Character.isLetterOrDigit(c) || Character.isWhitespace(c))) {
                return false;
            }
        }
        if (name.trim().length() == 0) {
            // Name length = 0
            return false;
        }
        this.players.put(player, player.getName());
        this.gameMap.put(player, GameCenter.EMPTY_GAME_KEY);
        log();
        return !ATTRIBUTES.contains(name); // make sure name is not also attribute
    }

    /**
     * @param name Name of {@Link Player}
     * @return True if exists in {@Linkplain PlayerLobby}
     */
    public boolean nameExists(String name) {
        return this.players.containsValue(name);
    }

    /**
     * Check to see if a {@Link Player}s name is too long
     *
     * @param name The {@Linkplain String} name of the {@Link Player}
     * @return True, if length is good
     */
    public boolean nameLengthCorrect(String name) {
        return name.length() <= MAX_NAME_LENGTH;
    }

    /**
     * Handle signing out of player from WebApp.
     *
     * @param player {@Link Player} that is attempting to sign out.
     */
    public void signOut(Player player) {
        this.signOut(player.getName());
        log();
    }

    /**
     * Handle signing out of player from WebApp.
     *
     * @param nameToRemove {@Linkplain String} is the name of the {@Link Player} signing out.
     * @return true if sign-out was successful.
     */
    public boolean signOut(String nameToRemove) {
        final Player[] player_ = new Player[1];
        this.players.forEach((player, name) -> {
            if (nameToRemove.equals(name)) {
                player_[0] = player;
            }
        });
        if (player_[0] != null) {
            Player player = player_[0];
            int id = this.gameMap.get(player);
            if (id != GameCenter.EMPTY_GAME_KEY) {
                this.gameFinished(id);
            }
            this.gameMap.remove(player);
            this.players.remove(player);
            log();
            return true;
        }
        return false;
    }

    /**
     * Get all {@Link Player} names signed on to WebApp.
     *
     * @return {@Linkplain Collection} of {@Link Player} names.
     */
    public Collection<String> getUserNamesList() {
        return this.players.values();
    }

    /**
     * Find out if a player is in the PlayerLobby or in game.
     *
     * @param player The {@Link Player} that the status is being checked.
     * @return The {@Link Player} that is the opponent of 'player'.
     */
    public Player getOpponentOf(Player player) {
        if (this.gameMap.containsKey(player)) {
            try {
                return this.gameCenter.getGame(this.gameMap.get(player)).getOppositePlayer(player);
            } catch (NullPointerException e) {
                return null;
                // Thrown when player not in game.
            }
        }
        return null;
    }

    /**
     * Find out if a player is in the PlayerLobby or in game.
     *
     * @param username The {@Linkplain String} username of the {@Link Player} that is being checked.
     * @return The {@Link Player} that is the opponent of the player given.
     */
    public Player getOpponentOf(String username) {
        for (Player player : this.players.keySet()) {
            if (player.getName().equals(username)) {
                return getOpponentOf(player);
            }
        }
        return null;
    }

    /**
     * Mark if a {@Link Player} is in a {@Link CheckersGame} or not.
     *
     * @param gameID {@Link Game} that has been started.
     */
    public void markPlayersInGame(int gameID) {
        this.gameMap.put(this.gameCenter.getGame(gameID).getRedPlayer(), gameID);
        this.gameMap.put(this.gameCenter.getGame(gameID).getWhitePlayer(), gameID);
        log();
    }

    /**
     * Mark a {@Link Player} as a spectator of a {@Link Game}.
     *
     * @param player {@Link Player} that is a spectator
     * @param gameID {@Link Game} that has been started.
     */
    public void markSpectator(Player player, int gameID) {
        this.spectatorMap.put(player.getName(), gameID);
    }

    /**
     * Remove a spectator from their game.
     *
     * @param player The {@Link Player} that is spectating a {@Link Game}.
     */
    public void removeSpectator(Player player) {
        this.spectatorMap.remove(player.getName());
    }

    /**
     * Remove a player and their partner from the game.
     *
     * @param gameID game id of the {@Link Ganme} that has ended
     */
    public void gameFinished(int gameID) {
        this.gameMap.put(this.gameCenter.getGame(gameID).getWhitePlayer(), GameCenter.EMPTY_GAME_KEY);
        this.gameMap.put(this.gameCenter.getGame(gameID).getRedPlayer(), GameCenter.EMPTY_GAME_KEY);
        log();
    }

    /**
     * Get a {@Link Player} by their username.
     *
     * @param username {@Linkplain String} username of player
     * @return {@Link Player} or null if does not exist
     */
    public Player getPlayer(String username) {
        for (Player player : this.players.keySet()) {
            if (player.getName().equals(username)) { // Search for player by username
                return player;
            }
        }
        return null;
    }

    /**
     * Get a {@Link Player}s game id
     *
     * @param player The {@Link Player} to check.
     * @return The players gameID for the {@Link GameCenter}
     */
    public int getPlayerGameID(Player player) {
        return this.gameMap.get(player);
    }

    /**
     * Log the maps to {@Link File}.
     */
    public void log() {
        if (STORE_CLIENTS) {
            try (
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(LOBBY_LOG_FILE_PATH)))
            ) {
                oos.writeObject(new HashMap[]{this.players, this.gameMap});
                oos.flush();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * @param player {@Link Player} name ({@Linkplain String}) being checked for being a spectator
     * @return True, if {@Link Player} is spectator.
     */
    public boolean isSpectator(String player) {
        return this.spectatorMap.containsKey(player);
    }

    /**
     * Get the game that a spectator is spectating.
     *
     * @param player The {@Linkplain String} name of {@Link Player}.
     * @return {@Linkplain Integer} - gameID
     */
    public int getGameSpectating(String player) {
        return this.spectatorMap.get(player);
    }

}
