package com.webcheckers.model;

import java.io.Serializable;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * A <code>Space</code> defines the "location" on the {@link BoardView}
 * where the game takes place. <code>Space</code>s can be dark or light, and
 * they can contain a {@link Piece} or not.
 *
 * @author <a href='mailto:np9379@rit.edu'>Nathan Page</a>
 * @see BoardView
 * @see Piece
 */
public class Space implements Serializable {

    /**
     * Defines the horizontal location of the <code>Space</code>
     */
    private int cellIdx;

    /**
     * These taken together describe the state of the <code>Space</code>
     * Dark, empty spaces can have <code>Piece</code>s placed upon them;
     * in any other state they cannot.
     */
    private boolean isDark = FALSE;
    private boolean isEmpty = TRUE;

    /**
     * This field is null if there is no <code>Piece</code> on the space
     */
    private Piece piece;

    /**
     * A constructor which produces a single light <code>Space</code>
     *
     * @param cellIdx horizontal location of the <code>Space</code>
     */
    public Space(int cellIdx) {
        this.cellIdx = cellIdx;
    }

    /**
     * A constructor which produces a <code>Space</code> that can be played upon (or not).
     *
     * @param cellIdx horizontal location of the <code>Space</code>
     * @param isDark  whether the <code>Space</code> is dark or light
     */
    public Space(int cellIdx, boolean isDark) {
        this.isDark = isDark;
        this.cellIdx = cellIdx;
    }

    /**
     * This method places a <code>Piece</code> on the <code>Space</code>,
     * if it <code>isValid</code>
     *
     * @param piece
     */
    public void setPiece(Piece piece) {
        if (this.isValid()) {
            this.piece = piece;
            isEmpty = false;
        }
    }

    /**
     * A method to remove a <code>Piece</code> from the <code>Space</code>
     */
    public void removePiece() {
        isEmpty = true;
        piece = null;
    }

    /**
     * A getter for the horizontal location of the <code>Space</code>
     *
     * @return the index of the <code>Space</code>
     */
    public int getCellIdx() {
        return cellIdx;
    }

    /**
     * This returns true only if the space is available to be played upon
     *
     * @return whether the space is both dark and empty
     */
    public boolean isValid() {
        return (isDark && isEmpty);
    }

    /**
     * A method to display the <code>Piece</code> on the <code>Space</code>
     *
     * @return the <code>Piece</code> in question (if it exists)
     */
    public Piece getPiece() {
        if (!isEmpty) {
            return piece;
        } else {
            return null;
        }
    }
}
