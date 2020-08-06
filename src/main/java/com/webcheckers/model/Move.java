package com.webcheckers.model;

import java.io.Serializable;

/**
 * A <code>Move</code> describes the transition of a {@link Piece}
 * from one {@link Position} on the {@link BoardView} to another.
 *
 * @author <a href='mailto:np9379@rit.edu>Nathan Page</a>
 * @see Position
 * @see Piece
 * @see BoardView
 */
public class Move implements Serializable {

    /**
     * The <code>Piece</code> begins its <code>Move</code> at one <code>Positon</code>
     */
    private Position start;
    /**
     * ... and finishes at another
     */
    private Position end;

    /**
     * Constructor which takes two positional arguments to describe the transition
     * of a <code>Piece</code> from one location to another
     *
     * @param start beginning <code>Position</code> of the <code>Piece</code>
     * @param end   final <code>Position</code> of the <code>Piece</code>
     */
    public Move(Position start, Position end) {
        this.start = start;
        this.end = end;
    }

    /**
     * A getter for the <code>start</code> of the movement
     *
     * @return the initial <code>Position</code> of the <code>Piece</code>
     */
    public Position getStart() {
        return start;
    }

    /**
     * A getter for the <code>end</code> of the movement
     *
     * @return the final <code>Position</code> of the <code>Piece</code>
     */
    public Position getEnd() {
        return end;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format("Move {\n\tFrom: %s\n\tTo: %s\n}", this.start.toString(), this.end.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Move) {
            return this.start == ((Move) obj).getStart() & this.end == ((Move) obj).getEnd();
        }
        return false;
    }
}
