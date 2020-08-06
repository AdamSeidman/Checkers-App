package com.webcheckers.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * A <code>Position</code> is the location of a piece on the {@link BoardView}
 *
 * @author <a href='mailto:np@9379@rit.edu>Nathan Page</a>
 * @see BoardView
 */
public class Position implements Serializable {

    /**
     * A <code>Position</code> is described by a <code>row</code>
     * and a <code>col</code> (column), like (x, y) on a grid
     */
    int row;
    int cell;

    /**
     * The constructor takes two positional arguments to describe
     * a location on a grid of {@link Space}s
     *
     * @param row  the horizontal determinant of the <code>Position</code>
     * @param cell the vertical determinant of the <code>Position</code>
     * @see Space
     */
    public Position(int row, int cell) {
        this.row = row;
        this.cell = cell;
    }

    /**
     * A getter for the <code>row</code> variable
     *
     * @return the row of the <code>Space</code> for this <code>Position</code>
     */
    public int getRow() {
        return row;
    }

    /**
     * A getter for the <code>col</code> variable
     *
     * @return the column of the <code>Space</code> for this <code>Position</code>
     */
    public int getCell() {
        return cell;
    }

    @Override
    public String toString() {
        return String.format("Row: %d, Cell: %d", row, cell);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Position) {
            return ((Position) obj).row == this.row && ((Position) obj).cell == this.cell;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.row, this.cell);
    }
}
