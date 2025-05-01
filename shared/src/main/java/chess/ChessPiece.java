package chess;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Math.abs;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor pieceColor;
    PieceType type;
    public ChessPiece(ChessGame.TeamColor pieceColor, PieceType type) {
        this.type = type;
        this.pieceColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();


        if (type == PieceType.BISHOP) {
            int temp_row;
            int temp_col;
            for (int x = -1; x < 2; x += 2) {
                for (int y = -1; y < 2; y += 2) {
                    temp_row = row + x;
                    temp_col = col + y;
                    while (temp_row > 0 && temp_row < 9 && temp_col > 0 && temp_col < 9) {
                        if (board.getPiece(new ChessPosition(temp_row, temp_col)) != null) {
                            if (board.getPiece(new ChessPosition(temp_row, temp_col)).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                                moves.add(new ChessMove(myPosition, new ChessPosition(temp_row, temp_col), null));
                            }
                            break;
                        } else {
                            moves.add(new ChessMove(myPosition, new ChessPosition(temp_row, temp_col), null));
                            System.out.println(temp_row+", "+temp_col);
                            if (board.getPiece(new ChessPosition(temp_row, temp_col)) != null) break;
                            temp_row += x;
                            temp_col += y;
                        }
                    }
                }
            }
        } else if (type == PieceType.KING) {
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    int temp_row = row + x;
                    int temp_col = col + y;
                    // If not out of bounds
                    if (temp_row > 0 && temp_row < 9 && temp_col > 0 && temp_col < 9) {
                        // If space is not occupied or occupied by enemy
                        if ((board.getPiece(new ChessPosition(temp_row, temp_col)) == null) || (board.getPiece(new ChessPosition(temp_row, temp_col)).getTeamColor() != board.getPiece(myPosition).getTeamColor()))  {
                            moves.add(new ChessMove(myPosition, new ChessPosition(temp_row, temp_col), null));
                        }
                    }

                }
            }
        } else if (type == PieceType.KNIGHT) {
            for (int x = -2; x < 3; x++) {
                for (int y = -2; y < 3; y++) {
                    if (x != 0 && y != 0 && abs(abs(x)-abs(y)) == 1) {
                        int temp_row = row + x;
                        int temp_col = col + y;
                        // If not out of bounds
                        if (temp_row > 0 && temp_row < 9 && temp_col > 0 && temp_col < 9) {
                            // If space is not occupied or occupied by enemy
                            if ((board.getPiece(new ChessPosition(temp_row, temp_col)) == null) || (board.getPiece(new ChessPosition(temp_row, temp_col)).getTeamColor() != board.getPiece(myPosition).getTeamColor()))  {
                                moves.add(new ChessMove(myPosition, new ChessPosition(temp_row, temp_col), null));
                            }
                        }
                    }
                }
            }
        } else if (type == PieceType.PAWN) {
            int direction;

            if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
                direction = -1;
            } else {
                direction = 1;
            }

            boolean earnsPromotion = (((direction == -1 && row == 2) || (direction == 1 && row == 7)));
            boolean firstMove = ((direction == -1 && row == 7) || (direction == 1 && row == 2));

            if ((board.getPiece(new ChessPosition(row + direction, col)) == null))  {
                moves.add(new ChessMove(myPosition, new ChessPosition(row + direction, col), null));

                if (firstMove && (board.getPiece(new ChessPosition(row + (2 * direction), col)) == null))  {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row + (2 * direction), col), null));
                }
            }

            if (col > 1 && (board.getPiece(new ChessPosition(row + direction, col - 1)) != null) && (board.getPiece(new ChessPosition(row + direction, col - 1)).getTeamColor() != board.getPiece(myPosition).getTeamColor()))  {
                moves.add(new ChessMove(myPosition, new ChessPosition(row + direction, col - 1), null));
            }
            if (col < 8 && (board.getPiece(new ChessPosition(row + direction, col + 1)) != null) && (board.getPiece(new ChessPosition(row + direction, col + 1)).getTeamColor() != board.getPiece(myPosition).getTeamColor()))  {
                moves.add(new ChessMove(myPosition, new ChessPosition(row + direction, col + 1), null));
            }

            if (earnsPromotion) {
                ArrayList<ChessMove> tempMoves = new ArrayList<ChessMove>();
                for (ChessMove move : moves) {
                    tempMoves.add(new ChessMove(myPosition, move.getEndPosition(), PieceType.QUEEN));
                    tempMoves.add(new ChessMove(myPosition, move.getEndPosition(), PieceType.BISHOP));
                    tempMoves.add(new ChessMove(myPosition, move.getEndPosition(), PieceType.ROOK));
                    tempMoves.add(new ChessMove(myPosition, move.getEndPosition(), PieceType.KNIGHT));
                }
                moves = tempMoves;
            }
        } else if (type == PieceType.QUEEN) {
            int temp_row;
            int temp_col;
            for (int x = -1; x < 2; x += 1) {
                for (int y = -1; y < 2; y += 1) {
                    temp_row = row + x;
                    temp_col = col + y;
                    while (temp_row > 0 && temp_row < 9 && temp_col > 0 && temp_col < 9) {
                        if (board.getPiece(new ChessPosition(temp_row, temp_col)) != null) {
                            if (board.getPiece(new ChessPosition(temp_row, temp_col)).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                                moves.add(new ChessMove(myPosition, new ChessPosition(temp_row, temp_col), null));
                            }
                            break;
                        } else {
                            moves.add(new ChessMove(myPosition, new ChessPosition(temp_row, temp_col), null));
                            System.out.println(temp_row+", "+temp_col);
                            if (board.getPiece(new ChessPosition(temp_row, temp_col)) != null) break;
                            temp_row += x;
                            temp_col += y;
                        }
                    }
                }
            }
        } else if (type == PieceType.ROOK) {
            int temp_row;
            int temp_col;
            int[] xList = {-1,1,0,0};
            int[] yList = {0,0,-1,1};
            for (int i = 0; i < 4; i++) {
                int x = xList[i];
                int y = yList[i];

                temp_row = row + x;
                temp_col = col + y;
                while (temp_row > 0 && temp_row < 9 && temp_col > 0 && temp_col < 9) {
                    if (board.getPiece(new ChessPosition(temp_row, temp_col)) != null) {
                        if (board.getPiece(new ChessPosition(temp_row, temp_col)).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(temp_row, temp_col), null));
                        }
                        break;
                    } else {
                        moves.add(new ChessMove(myPosition, new ChessPosition(temp_row, temp_col), null));
                        System.out.println(temp_row+", "+temp_col);
                        if (board.getPiece(new ChessPosition(temp_row, temp_col)) != null) break;
                        temp_row += x;
                        temp_col += y;
                    }
                }

            }
        }



        return moves;
    }
}
