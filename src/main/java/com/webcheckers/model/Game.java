package com.webcheckers.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.webcheckers.util.Attributes.*;
import static java.lang.Boolean.TRUE;

/**
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 */
public class Game implements Serializable {
    public enum viewMode {
        PLAY, SPECTATOR
    }

    private enum MoveType {
        JUMP, SINGLE, NONE
    }

    private Player redPlayer;
    private Player whitePlayer;
    private BoardView board;
    private BoardView flippedBoard;
    private List<Position> removedPieces;
    private MoveType firstMove = MoveType.NONE;
    private boolean redActive = true;
    private boolean winnerRed = false;
    private boolean moveFlag = false;

    public Game(Player redPlayer, Player whitePlayer) {
        this.redPlayer = redPlayer;
        this.whitePlayer = whitePlayer;
        this.board = new BoardView(false);
        this.flippedBoard = new BoardView(true);
        this.removedPieces = new ArrayList<>();
    }

    public Player getRedPlayer() {
        return redPlayer;
    }

    public Player getWhitePlayer() {
        return whitePlayer;
    }

    public BoardView getBoard(boolean flipped) {
        return flipped ? this.flippedBoard : board;
    }

    public Piece.Color getColor() {
        return redActive ? Piece.Color.RED : Piece.Color.WHITE;
    }

    /**
     * Switch the active player
     */
    public void changePlayer() {
        if (redActive) {
            redActive = false;
        } else {
            redActive = true;
        }
    }

    /**
     * Get the attributes that would supply a game page to a user.
     *
     * @param currentPlayer The current {@Link Player}
     * @return {@Linkplain Map}
     */
    public Map<String, Object> getGameAttributes(Player currentPlayer) {
        return this.getGameAttributes(currentPlayer, currentPlayer.equals(whitePlayer));
    }

    /**
     * Get the attributes that would supply a game page to a user.
     * typically a spectator
     *
     * @param currentPlayer The current {@Link Player}
     * @param boardFlipped  True if {@Link BoardView} is needed flipped.
     * @return {@Linkplain Map}
     */
    public Map<String, Object> getGameAttributes(Player currentPlayer, boolean boardFlipped) {
        Map<String, Object> vm = new HashMap<>();

        if (redActive) {
            vm.put(CURRENT_PLAYER_ATTR, redPlayer);
            vm.put(ACTIVE_COLOR_ATTR, Piece.Color.RED);
        } else {
            vm.put(CURRENT_PLAYER_ATTR, whitePlayer);
            vm.put(ACTIVE_COLOR_ATTR, Piece.Color.WHITE);
        }

        boolean modeIsPlay = currentPlayer.equals(redPlayer) || currentPlayer.equals(whitePlayer);

        vm.put(CURRENT_PLAYER_ATTR, currentPlayer);
        vm.put(VIEW_MODE_ATTR, modeIsPlay ? viewMode.PLAY : viewMode.SPECTATOR);
        vm.put(RED_PLAYER_ATTR, redPlayer);
        vm.put(WHITE_PLAYER_ATTR, whitePlayer);
        vm.put(BOARD_ATTR, this.getBoard(boardFlipped));

        return vm;
    }

    public Player getOppositePlayer(Player player) {
        if (!player.equals(this.redPlayer)) {
            return this.redPlayer;
        }
        return this.whitePlayer;
    }

    /**
     * {@see BoardView.submitMoves()}
     *
     * @return True, if there are moves in either.
     */
    public boolean submitMoves() {
        this.moveFlag = TRUE;
        this.removedPieces = new ArrayList<>();
        this.firstMove = MoveType.NONE;
        return flippedBoard.submitMoves() | board.submitMoves();
    }

    /**
     * Add a move to the move queue
     *
     * @param move move to be added
     */
    public void addMove(Move move) {
        if (Math.abs(move.getStart().getRow() - move.getEnd().getRow()) == 2) {
            this.firstMove = MoveType.JUMP;
        } else {
            this.firstMove = MoveType.SINGLE;
        }
        this.board.addMove(move);
        this.flippedBoard.addMove(move);
        Position start = move.getStart();
        Position end = move.getEnd();
        if (Math.abs(start.getRow() - end.getRow()) == 2) {
            // Add a removed piece to a list of jumped pieces.
            this.removedPieces.add(new Position(start.getRow() + ((end.getRow() - start.getRow()) / 2),
                    start.getCell() + ((end.getCell() - start.getCell()) / 2)));
        }
    }

    /**
     * Check to see if a position of an opponent's {@Link Piece} has been removed form the {@Link BoardView}
     *
     * @param position The {@Link Position} the opponents {@Link Piece} is sitting on.
     * @return True, if {@Link Piece} has been taken off of {@Link Space}
     */
    public boolean posRemoved(Position position) {
        return this.removedPieces.contains(position);
    }

    /**
     * Retrieve most recent move from queue
     *
     * @return [most recent move, flipped]
     */
    public Move[] getMove() {
        return new Move[]{this.board.getMove(), this.flippedBoard.getMove()};
    }

    /**
     * Move has been built completely based off of number of spaces move
     *
     * @param numSpaces Number of (@Link Rows) moved in next move
     * @return True if based off params, can't move any more {@Link Space}s.
     */
    public boolean moveBuildComplete(int numSpaces) {
        return !(this.firstMove.equals(MoveType.NONE) || (numSpaces == 2 && this.firstMove.equals(MoveType.JUMP)));
    }

    /**
     * Back up one move
     */
    public void backUp() {
        this.getMove();
        if (this.board.numOfMovesMade() == 0) {
            this.firstMove = MoveType.NONE;
        }
        if (this.removedPieces.size() > 0) {
            this.removedPieces.remove(this.removedPieces.size() - 1);
        }
    }

    /**
     * Get the inital {@Link Position} if a {@Link Move} has been made.
     *
     * @param defaultPos Default {@Link Position} if no initial position.
     * @return {@Link Move}
     */
    public Position getInitialPosition(Position defaultPos) {
        return this.board.getInitialPosition() == null ? defaultPos : this.board.getInitialPosition();
    }

    /**
     * Find out if specified {@Link Player} is active.
     *
     * @param player {@Link Player} being checked.
     * @return True, if active.
     */
    public boolean isActivePlayer(Player player) {
        Player activePlayer = this.whitePlayer;
        if (this.redActive) {
            activePlayer = this.redPlayer;
        }
        return activePlayer.equals(player);
    }

    /**
     * Find out if the {@Linkplain Game} has completed.
     *
     * @return True, if the game is over.
     */
    public boolean isOver() {
        boolean foundRed = false;
        boolean foundWhite = false;
        for (Row aBoard : this.board) {
            for (Space anABoard : aBoard) {
                Piece piece = anABoard.getPiece();
                if (piece != null) {
                    if (piece.getColor().equals(Piece.Color.RED)) {
                        foundRed = true;
                    } else if (piece.getColor().equals(Piece.Color.WHITE)) {
                        foundWhite = true;
                    }
                }
            }
        }
        if (!foundRed || !foundWhite) {
            winnerRed = foundRed;
        }
        return !foundRed || !foundWhite;
    }

    /**
     * Get the {@Link Piece.Color} of the winner
     *
     * @return The Color of the Checkers game winner.
     */
    public Piece.Color getWinnerColor() {
        return winnerRed ? Piece.Color.RED : Piece.Color.WHITE;
    }

    /**
     * @return True, if a {@Link Move} has been made since the last time checked.
     */
    public boolean moveMade() {
        boolean ret = this.moveFlag;
        this.moveFlag = false;
        return ret;
    }

    /**
     * run this method on page reload
     */
    public void reload() {
        this.removedPieces = new ArrayList<>();
    }

}
