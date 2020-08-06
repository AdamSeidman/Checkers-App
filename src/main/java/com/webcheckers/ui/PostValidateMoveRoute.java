package com.webcheckers.ui;

import com.google.gson.Gson;
import com.webcheckers.appl.GameCenter;
import com.webcheckers.model.*;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Session;

import java.util.ArrayList;
import java.util.List;

import static com.webcheckers.util.Attributes.*;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * @author <a href="mailto:ajs1551@rit.edu">Adam Seidman</a>
 * @author <a href='mailto:np9379@rit.edu'>Nathan Page</a>
 */
public class PostValidateMoveRoute implements Route {
    private final Gson gson;
    private final GameCenter gameCenter;
    private boolean alreadyJumped = false;

    private Session httpSession;

    public PostValidateMoveRoute(Gson gson, GameCenter gameCenter) {
        this.gson = gson;
        this.gameCenter = gameCenter;
    }

    @Override
    public Object handle(Request request, Response response) throws Exception {
        this.httpSession = request.session();

        String data = request.body();
        Move move = gson.fromJson(data, Move.class);

        Message message = validateMove(move, httpSession.attribute(GAME_ID_ATTR));
        return gson.toJson(message);
    }

    private Message validateMove(Move move, int gameID) {
        Game game = this.gameCenter.getGame(gameID);
        if (game == null) {
            return new Message(MID_GAME_PARTNER_RESIGN_MSG, Message.Type.info);
        } else {
            boolean hasBetterMove = false;
            try {
                hasBetterMove = this.hasBetterMove(move, game.getBoard(FALSE), game.getBoard(FALSE).getRowAt(move.getStart().getRow())
                        .getCellAt(move.getStart().getCell()).getPiece().getColor());
            } catch (NullPointerException e) {
            }
            if (game.moveBuildComplete(Math.abs(move.getStart().getRow() - move.getEnd().getRow()))) {
                return new Message(TOO_MANY_MOVES_MSG, Message.Type.error);
            } else if (hasBetterMove) {
                return new Message(JUMP_MOVE_AVAILABLE_MSG, Message.Type.error);
            } else if (moveIsValid(move.getStart(), move.getEnd(), game, httpSession.attribute(CURRENT_PLAYER_ATTR))) {
                game.addMove(move);
                return new Message(GOOD_MOVE_MSG, Message.Type.info);
            } else if (this.alreadyJumped) {
                this.alreadyJumped = FALSE;
                return new Message(ALREADY_JUMPED_MSG, Message.Type.error);
            } else {
                return new Message(BAD_MOVE_MSG, Message.Type.error);
            }
        }

    }

    private boolean moveIsValid(Position start, Position end, Game game, Player currentPlayer) {
        Position initial = game.getInitialPosition(start);
        boolean isKing = game.getBoard(FALSE).getRowAt(initial.getRow()).getCellAt(
                initial.getCell()).getPiece().getType().equals(Piece.Type.KING);
        boolean isRed = game.getRedPlayer().equals(currentPlayer);
        int rowChange = end.getRow() - start.getRow();
        int cellChange = end.getCell() - start.getCell();
        if (Math.abs(rowChange) > 1) {
            if (Math.abs(rowChange) > 2 || Math.abs(cellChange) > 2) {
                // Cannot move more than 2 spaces.
                return FALSE;
            }
            try {
                // Return true if piece between diagonal is opposite color and the piece can move there
                Piece opponentPiece = game.getBoard(FALSE).getRowAt(start.getRow() + (rowChange / 2)).getCellAt(
                        start.getCell() + (cellChange / 2)).getPiece();
                if (game.posRemoved(new Position(start.getRow() + (rowChange / 2), start.getCell() + (cellChange / 2)))) {
                    this.alreadyJumped = TRUE;
                    return FALSE;
                }
                return !opponentPiece.getColor().equals(game.getBoard(FALSE).getRowAt(initial.getRow()).getCellAt(
                        initial.getCell()).getPiece().getColor()) && (isKing || (isRed && rowChange == -2) ||
                        (!isRed && rowChange == 2));
            } catch (NullPointerException e) {
                // There was no piece where the player tried to jump
                return FALSE;
            }
        } else if (isKing) {
            // Is king and allowed to move where ever.
            return Math.abs(rowChange) == 1 && Math.abs(cellChange) == 1;
        } else {
            // Normal piece moving forward one space.
            return Math.abs(cellChange) == 1 && ((isRed && rowChange == -1) || (!isRed && rowChange == 1));
        }
    }

    private boolean hasBetterMove(Move move, BoardView board, Piece.Color color) {
        List<Position> jumpPosList = new ArrayList<>();
        for (Row row_ : board) {
            for (Space space : row_) {
                // Check each piece on board.
                Position start = new Position(row_.getIndex(), space.getCellIdx());
                try {
                    rowLoop:
                    for (int rowSub = start.getRow() - 2; rowSub <= start.getRow() + ((board.getRowAt(start.getRow()).getCellAt(
                            start.getCell()).getPiece().getType().equals(Piece.Type.KING)) ? 2 : -2); rowSub += 4) {
                        // Check each row and each cell for possible jump moves
                        int row = rowSub;
                        if (color.equals(Piece.Color.WHITE) && board.getRowAt(start.getRow()).getCellAt(start.getCell())
                                .getPiece().getType().equals(Piece.Type.SINGLE)) {
                            row += 4;
                        }
                        for (int cell = start.getCell() - 2; cell <= start.getCell() + 2; cell += 4) {
                            try {
                                // Get all spaces in the interaction
                                Space startSpace = board.getRowAt(start.getRow()).getCellAt(start.getCell());
                                if (startSpace.getPiece() == null || !startSpace.getPiece().getColor().equals(color)) {
                                    break rowLoop;
                                }
                                Space opponentSpace = board.getRowAt(start.getRow() + ((row - start.getRow()) / 2))
                                        .getCellAt(start.getCell() + ((cell - start.getCell()) / 2));
                                Space newSpace = board.getRowAt(row).getCellAt(cell);
                                if (newSpace.getPiece() == null && opponentSpace.getPiece() != null && !opponentSpace.getPiece().getColor().equals(startSpace.getPiece().getColor())) {
                                    // if its a valid jump move, put it in a list.
                                    jumpPosList.add(new Position(row, cell));
                                }
                            } catch (IndexOutOfBoundsException | NullPointerException ignored) {
                                // Occurs on no start piece
                            }
                        }
                    }
                } catch (NullPointerException ignored) {
                }
            }
        }
        // True if jump move exists and is not taken
        return jumpPosList.size() > 0 && !jumpPosList.contains(move.getEnd());
    }
}
