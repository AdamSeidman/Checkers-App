package com.webcheckers.model;

import java.io.Serializable;
import java.util.Iterator;

import static com.webcheckers.util.Attributes.CHECKERBOARD_SPACES;
import static java.lang.Boolean.TRUE;

/**
 * <code>Row</code> is the second level of the {@link BoardView} compound object. A <code>Row</code>
 * contains an array of eight {@link Space}s. It implements {@link Iterable} so that it can be
 * displayed by FreeMarker.
 *
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 * @author <a href='mailto:np9379@rit.edu'>Nathan Page</a>
 * @see BoardView
 * @see Space
 */
public class Row implements Iterable<Space>, Serializable {

    /**
     * <code>index</code> is the vertical location of the <code>Row</code> in question
     */
    private int index;
    private boolean isFlipped;

    /**
     * The array of <code>Space</code>s to be filled by the constructor
     */
    private Space[] spaces = new Space[CHECKERBOARD_SPACES];

    /**
     * Create a new <code>Row</code> for the {@link BoardView}. Each <code>Row</code>
     * is composed of 8 {@link Space}s, alternating dark and light. At the moment this
     * constructor also sets up the game to be played. This may be changed in subsequent
     * versions.
     *
     * @param index vertical position of the <code>Row</code> on the <code>BoardView</code>
     * @see BoardView
     * @see Space
     */
    public Row(int index, boolean isFlipped) {
        this.index = index;
        this.isFlipped = isFlipped;
        for (int i = 0; i < CHECKERBOARD_SPACES; i++) {
            // Alternating spaces on each row are dark and therefore valid locations to set a piece.
            if (((index % 2 == 0 && i % 2 == 1) || (index % 2 == 1 && i % 2 == 0)) && (index > 4)) {
                // Dark space with red piece
                Space s = new Space(i, TRUE);
                s.setPiece(new Piece(Piece.Type.SINGLE, Piece.Color.RED));
                spaces[i] = s;
            } else if (((index % 2 == 0 && i % 2 == 1) || (index % 2 == 1 && i % 2 == 0)) && (index < 3)) {
                // Dark space with white piece
                Space s = new Space(i, TRUE);
                s.setPiece(new Piece(Piece.Type.SINGLE, Piece.Color.WHITE));
                spaces[i] = s;
                // In set up, the two middle rows are left empty
            } else if ((index == 4 && i % 2 == 1) || (index == 3 && i % 2 == 0)) {
                // Dark space with no piece
                spaces[i] = new Space(i, TRUE);
            } else {
                // light space (no piece)
                spaces[i] = new Space(i);
            }
        }
    }

    /**
     * A getter for the <code>index</code> of a <code>Row</code>
     *
     * @return the vertical position of the <code>Row</code>
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns a {@code Space} at a given index
     *
     * @return cell at index
     */
    public Space getCellAt(int index) {
        return spaces[index];
    }

    /**
     * Creates an {@linkplain Iterator} to traverse across the <code>Row</code>s
     *
     * @return a <code>SpaceIterator</code>
     * @see SpaceIterator
     */
    @Override
    public Iterator<Space> iterator() {
        return new SpaceIterator(this.isFlipped);
    }

    /**
     * Inner class <code>SpaceIterator</code> implements {@linkplain Iterable}
     * to generate an <code>iterator</code>
     */
    class SpaceIterator implements Iterator<Space> {
        private int current;
        private boolean isFlipped;

        SpaceIterator(boolean isFlipped) {
            current = (isFlipped ? CHECKERBOARD_SPACES : 0);
            this.isFlipped = isFlipped;
        }


        @Override
        public boolean hasNext() {
            if (isFlipped) {
                if (current <= 0) {
                    current = CHECKERBOARD_SPACES;
                    return false;
                }
                return true;
            } else {
                if (current >= CHECKERBOARD_SPACES) {
                    current = 0;
                    return false;
                }
                return true;
            }
        }

        @Override
        public Space next() {
            if (isFlipped) {
                return spaces[--current];
            } else {
                return spaces[current++];
            }
        }
    }

    /**
     * Add a piece to the row
     *
     * @param piece The {@Link Piece} to be added
     * @param space The number of {@Linkplain int} space it is moving ot on the {@Link Row}.
     */
    public void addPiece(Piece piece, int space) {
        spaces[space].setPiece(piece);
    }

    /**
     * Remove a piece from the row.
     *
     * @param space The {@Linkplain int} space to remove it from.
     */
    public void removePiece(int space) {
        spaces[space].removePiece();
    }

}
