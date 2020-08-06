package com.webcheckers.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.logging.Logger;

import static com.webcheckers.util.Attributes.PLAYER_CREATED_MSG;

/**
 * A single player
 *
 * @author <a href='mailto:agr3714@rit.edu'>Audrey Rovero</a>
 * @author <a href='mailto:np9379@rit.edu'>Nathan Page</a>
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 */
public class Player implements Serializable {

    private static final Logger LOG = Logger.getLogger(Player.class.getName());
    private final String userName;

    /**
     * Creates a player with the given username.
     *
     * @param userName The username for the player.
     * @throws IllegalArgumentException when the {@code userName} is blank
     */

    public Player(final String userName) {
        // validate arguments
        LOG.fine(PLAYER_CREATED_MSG + userName);
        this.userName = userName;
    }

    /**
     * Returns the username of the player.
     *
     * @return userName of the player
     */
    public String getName() {
        return userName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String toString() {
        return "{Player " + userName + "}";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        String str;
        if (obj instanceof Player) {
            str = ((Player) obj).getName();
        } else if (obj instanceof String) {
            str = obj.toString();
        } else {
            return false;
        }
        return str.equals(this.getName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.userName);
    }

}
