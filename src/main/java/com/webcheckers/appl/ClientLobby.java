package com.webcheckers.appl;

import com.webcheckers.model.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.webcheckers.util.Attributes.*;

/**
 * A lobby to map players to their IP addresses.
 *
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 */
public class ClientLobby {

    private Map<String, String> addressMap;
    private Map<String, Long> logoutTimes;

    /**
     * Instantiate a new {@Linkplain ClientLobby}
     */
    public ClientLobby() {
        this.addressMap = new HashMap<>();
        this.logoutTimes = new HashMap<>();
        this.addressMap.put(SESSION_ID_ATTR, Long.toString(SESSION_ID));
    }

    /**
     * Instantiate a new {@Linkplain ClientLobby} from an existing {@Linkplain File}.
     *
     * @param filePath The path the current server data is located at.
     */
    public ClientLobby(String filePath) {
        File readFile = new File(filePath);
        try (
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(readFile))
        ) {
            Map[] maps = ((Map[]) ois.readObject());
            this.addressMap = maps[0];
            this.logoutTimes = maps[1];
            try {
                SESSION_ID = Long.parseLong(this.addressMap.get(SESSION_ID_ATTR));
            } catch (NullPointerException ignored) {
            }
        } catch (ClassNotFoundException | IOException | NullPointerException e) {
            if(readFile.exists()) {
                readFile.delete();
            }
            this.addressMap = new HashMap<>();
            this.logoutTimes = new HashMap<>();
        }
        this.addressMap.put(SESSION_ID_ATTR, Long.toString(SESSION_ID));
        this.log();
    }

    /**
     * Sign a {@Link Player} into the {@Linkplain ClientLobby}
     *
     * @param ip   The ip address of the client.
     * @param name The name of the client's {@Link Player}.
     */
    public void signIn(String ip, String name) {
        this.addressMap.put(ip, name);
        this.logoutTimes.put(name, System.currentTimeMillis());
        log();
    }

    /**
     * Sign a {@Link Player} out of the {@Linkplain ClientLobby}
     *
     * @param nameOrIP The name of the {@Link Player} signing out or the IP address of the client.
     */
    public void signOut(String nameOrIP) {
        for (String ip : this.addressMap.keySet()) {
            // If map contains ip or name, remove it.
            if (ip.equals(nameOrIP) || this.addressMap.get(ip).equals(nameOrIP)) {
                this.addressMap.remove(ip);
                break;
            }
        }
        log();
    }

    /**
     * Log the address map to {@Link File}.
     */
    public void log() {
        if (STORE_CLIENTS) {
            try (
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(CLIENT_LOG_FILE_PATH)))
            ) {
                oos.writeObject(new Map[]{this.addressMap, this.logoutTimes});
                oos.flush();
            } catch (IOException ignored) {
            }
        }
    }

    /**
     * @param ip {@Linkplain String} IP address of a client
     * @return True, if client lobby has that IP.
     */
    public boolean has(String ip) {
        return this.addressMap.containsKey(ip);
    }

    /**
     * Get the {@Link Player} linked to a given IP address.
     *
     * @param ip The given IP address.
     * @return The {@Link Player} in the address map or null.
     */
    public Player get(String ip) {
        return new Player(this.addressMap.get(ip));
    }

    /**
     * Get a list of players being tracked by the {@Linkplain ClientLobby}
     *
     * @return {@Linkplain List} of {@Link Player}s
     */
    public List<Player> getPlayers() {
        List<Player> ret = new ArrayList<>();
        for (String name : this.addressMap.values()) {
            if (!name.equals(Long.toString(SESSION_ID))) {
                // Make list of new Players that aren't sessionID
                ret.add(new Player(name));
            }
        }
        return ret;
    }

    /**
     * Update a specific {@Link Player}s logout time.
     *
     * @param player {@Linkplain String}: Name of {@Link Player} being logged
     */
    public void updatePlayerTime(String player) {
        this.logoutTimes.put(player, System.currentTimeMillis());
    }

    /**
     * Update a specific {@Link Player}s logout time.
     *
     * @param player {@Link Player} being logged
     */
    public void updatePlayerTime(Player player) {
        this.updatePlayerTime(player.getName());
    }

    /**
     * Get list of {@Link Player}s that have been offline for some time.
     *
     * @return {@Linkplain ArrayList} of {@Link Player}s
     */
    public ArrayList<String> getExpiredPlayers() {
        ArrayList<String> ret = new ArrayList<>();
        for (String name : this.logoutTimes.keySet()) {
            long time = System.currentTimeMillis() - this.logoutTimes.get(name);
            if (time >= EXPIRE_TIME) {
                ret.add(name);
            }
        }
        return ret;
    }

    public Map<String, String> getAddressMap() {
        return addressMap;
    }

    public Map<String, Long> getLogoutTimes() {
        return logoutTimes;
    }
}
