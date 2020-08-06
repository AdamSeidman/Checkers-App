package com.webcheckers.appl;

import com.webcheckers.model.Game;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.webcheckers.util.Attributes.GAME_LOG_FILE_PATH;
import static com.webcheckers.util.Attributes.STORE_CLIENTS;

/**
 * Object that holds all games
 *
 * @author <a href="mailto:np9379@rit.edu">Nathan Page</a>
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 */
public class GameCenter {
    static final int EMPTY_GAME_KEY = 0;

    private Map<Integer, Game> gameMap;
    private List<Integer> resignList;

    public GameCenter() {
        File readFile = new File(GAME_LOG_FILE_PATH);
        try (
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(readFile))
        ) {
            if (!STORE_CLIENTS) {
                // Create new collections rather than reading
                throw new IOException();
            }
            Object[] collections = (Object[]) ois.readObject();
            this.gameMap = (Map<Integer, Game>) collections[0];
            this.resignList = (List<Integer>) collections[1];
        } catch (IOException | ClassNotFoundException e) {
            // Make sure new collections exist
            if(readFile.exists()) {
                readFile.delete();
            }
            this.gameMap = new HashMap<>();
            this.resignList = new ArrayList<>();
        }

        this.gameMap.put(EMPTY_GAME_KEY, null);
    }

    /**
     * @param id   ID number of game to add.
     * @param game {@Link Game} that is being added to GameCenter
     * @return True, if added.
     */
    public boolean addGame(int id, Game game) {
        if (gameMap.containsKey(id)) {
            return false;
        }
        gameMap.put(id, game);
        this.log();
        return true;
    }

    public Game getGame(int id) {
        return gameMap.get(id);
    }

    /**
     * Remove a {@Link Game} from the {@Linkplain GameCenter}
     *
     * @param id     The id of the {@Link Game}
     * @param resign True if this is the result of a {@Link Player} resignation.
     */
    public void removeGame(int id, boolean resign) {
        if (id != GameCenter.EMPTY_GAME_KEY && this.gameMap.containsKey(id)) {
            this.gameMap.remove(id);
            if (resign) {
                this.resignList.add(id);
            }
        }
        this.log();
    }

    /**
     * Check out a previously resigned {@Link Game} from the {@Linkplain GameCenter}
     *
     * @param id The id number of the {@Link Game}.
     * @return True, if the resignation ID is found in the {@Linkplain GameCenter}
     */
    public boolean checkout(int id) {
        if (this.resignList.contains(id)) {
            this.resignList.remove(new Integer(id));
            this.log();
            return true;
        }
        return false;
    }

    /**
     * Log the maps to {@Link File}.
     */
    public void log() {
        if (STORE_CLIENTS) {
            try (
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(GAME_LOG_FILE_PATH)))
            ) {
                oos.writeObject(new Object[]{this.gameMap, this.resignList});
                oos.flush();
            } catch (IOException ignored) {
            }
        }
    }
}
