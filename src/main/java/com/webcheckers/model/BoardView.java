package com.webcheckers.model;

import com.webcheckers.util.Attributes;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;

import static com.webcheckers.util.Attributes.CHECKERBOARD_ROWS;

/**
 * A <code>BoardView</code> is composed of an array of {@link Row}s which
 * are in turn composed of an array of {@link Space}s. Taken together
 * <code>Row</code> by <code>Space</code>, an 8 by 8 matrix is formed
 * representing a checkerboard. It implements {@linkplain Iterable} so that
 * it can be displayed by FreeMarker.
 *
 * @author <a href='mailto:ajs1551@rit.edu'>Adam Seidman</a>
 * @author <a href='mailto:np9379@rit.edu'>Nathan Page</a>
 * @see Row
 * @see Space
 */
public class BoardView implements Iterable<Row>, Serializable {

    // Attributes

    /**
     * A boolean that indicates whether the board is flipped or not
     * false = the board is in the red player's perspective
     * true = the board is in the white player's perspective
     */
    private boolean isFlipped;

    /**
     * Queue of moves to be transmitted between players
     */
    private LinkedList<Move> moves = new LinkedList<>();

    /**
     * an array of <code>Row</code> objects to be filled by the constructor
     */
    private Row[] rows = new Row[CHECKERBOARD_ROWS];

    /**
     * Create a new <code>Boardview</code> by instantiating 8 <code>Row</code>s and
     * compiling them into an array.
     */
    public BoardView(boolean isFlipped) {
        this.isFlipped = isFlipped;
        for (int i = 0; i < CHECKERBOARD_ROWS; i++) {
            Row r = new Row(i, this.isFlipped);
            rows[i] = r;
        }
    }

    /**
     * Returns true if board is flipped (for white player)
     *
     * @return true if board is flipped
     */
    public boolean isFlipped() {
        return isFlipped;
    }

    /**
     * Returns the row at a given index
     *
     * @param index location of the row called for
     * @return the row called for
     */
    public Row getRowAt(int index) {
        return rows[index];
    }

    /**
     * Returns the space at a given index
     *
     * @param x x coordinate of piece
     * @param y y coordinate of piece
     * @return Space at given coordinates
     */
    private Space getCellAt(int x, int y) {
        return rows[x].getCellAt(y);
    }

    /**
     * Returns piece on given space
     *
     * @param x x coordinate of piece
     * @param y y coordinate of piece
     * @return Piece at given coordinates
     */
    private Piece getPieceAt(int x, int y) {
        return this.getCellAt(x, y).getPiece();
    }

    /**
     * Removes and returns the piece at a given space
     *
     * @param x x coordinate of piece/space
     * @param y y coordinate of piece/space
     * @return piece removed from space
     */
    public Piece removePieceAt(int x, int y) {
        Piece piece = this.getPieceAt(x, y);
        getCellAt(x, y).removePiece();

        return piece;
    }

    /**
     * Places a piece on a given space
     *
     * @param x     x coordinate of space to be played upon
     * @param y     y coordinate of space to be played upon
     * @param piece piece to be played upon given space
     */
    public void setPieceAt(int x, int y, Piece piece) {
        if (getCellAt(x, y).isValid())
            getCellAt(x, y).setPiece(piece);
    }

    /**
     * Add a move to the move queue
     *
     * @param move move to be added
     */
    public void addMove(Move move) {
        moves.add(move);
    }

    /**
     * Retrieve most recent move from queue
     *
     * @return most recent move
     */
    public Move getMove() {
        return moves.poll();
    }

    /**
     * Submit all moves in the queue
     *
     * @return True, if there were moves to make
     */
    public boolean submitMoves() {
        if (this.moves.size() == 0) {
            return false;
        }
        while (moves.size() > 0) {
            // While there are moves in the queue, remove them and adjust the board accordingly.
            Move move = this.getMove();
            int rowStart = move.getStart().getRow();
            int cellStart = move.getStart().getCell();
            int rowEnd = move.getEnd().getRow();
            int cellEnd = move.getEnd().getCell();
            if (Math.abs(rowEnd - rowStart) == 2) {
                // Remove a piece if it is jumped.
                this.removePieceAt(rowStart + ((rowEnd - rowStart) / 2), cellStart + ((cellEnd - cellStart) / 2));
            }
            this.setPieceAt(rowEnd, cellEnd, this.removePieceAt(rowStart, cellStart));
            if (rowEnd % (Attributes.CHECKERBOARD_ROWS - 1) == 0) {
                this.getPieceAt(rowEnd, cellEnd).kingMe();
            }
        }
        return true;
    }

    /**
     * @return the number of moves currently made on this {@LinkPlain BoardView}
     */
    public int numOfMovesMade() {
        return this.moves.size();
    }

    /**
     * Get the initialPosition of the current {@Link Move}s
     *
     * @return {@Link Position}
     */
    public Position getInitialPosition() {
        if (this.moves.size() == 0) {
            return null;
        }
        return this.moves.peekFirst().getStart();
    }

    /**
     * Creates an Iterator to traverse the <code>Row</code>s
     *
     * @return a <code>RowIterator</code>
     * @see RowIterator
     */
    @Override
    public Iterator<Row> iterator() {
        return new RowIterator();
    }

    /**
     * Inner class <code>RowIterator</code> implements {@linkplain Iterable} to generate an
     * <code>iterator</code>
     */
    class RowIterator implements Iterator<Row> {
        private int current;

        public RowIterator() {
            if (isFlipped) {
                this.current = CHECKERBOARD_ROWS;
            } else {
                this.current = -1;
            }
        }


        @Override
        public boolean hasNext() {
            if (isFlipped) {
                if (current <= 0) {
                    current = CHECKERBOARD_ROWS;
                    return false;
                }
                return true;
            } else {
                if (current >= (CHECKERBOARD_ROWS - 1)) {
                    current = -1;
                    return false;
                }
                return true;
            }

        }

        @Override
        public Row next() {

            if (isFlipped) {
                current--;
            } else {
                current++;
            }
            return rows[current];
        }
    }
}
