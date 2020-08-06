package com.webcheckers.model;

import java.io.Serializable;

/**
 * The <code>Piece</code> represents a piece on a checkerboard, it is what makes
 * checkers a game, with the object of capturing all of your opponents <code>Piece</code>s.
 *
 * @author <a href='mailto:np9379@rit.edu'>Nathan Page</a>
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 */
public class Piece implements Serializable {

    /**
     * enums which describe the range of possibilities for a <code>Piece</code>,
     * as in: SINGLE or KING, RED or WHITE
     */
    public enum Type {
        SINGLE, KING
    }

    public enum Color {RED, WHITE}

    /**
     * these describe a single <code>Piece</code>
     */
    private Type pieceType;
    private Color pieceColor;

    /**
     * Constructor for a <code>Piece</code>
     *
     * @param pieceType  type of a <code>Piece</code>
     * @param pieceColor the color of a <code>Piece</code>
     */
    public Piece(Type pieceType, Color pieceColor) {
        this.pieceType = pieceType;
        this.pieceColor = pieceColor;
    }

    /**
     * A getter for the type of the <code>Piece</code> in question,
     * SINGLE or KING
     *
     * @return <code>pieceType</code> of a <code>Piece</code>
     */
    public Type getType() {
        return pieceType;
    }

    /**
     * A getter for the Color of the <code>Piece</code> in question,
     * RED or WHITE
     *
     * @return <code>pieceColor</code> of a <code>Piece</code>
     */
    public Color getColor() {
        return pieceColor;
    }

    /**
     * Set piece {@Link Type} to King.
     */
    public void kingMe() {
        this.pieceType = Type.KING;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized String toString() {
        return "{Piece " + pieceType + "," + pieceColor + "}";
    }
}
