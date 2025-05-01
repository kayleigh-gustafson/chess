package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
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

        // Stores valid moves
        ArrayList<ChessMove> moves = new ArrayList<ChessMove>();

        // Saves row and column of starting position for easier access
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Bishop
        if (type == PieceType.BISHOP) {
            // Row and column of potential moves
            int temp_row;
            int temp_col;
            // For x = -1 and x = 1
            for (int x = -1; x < 2; x += 2) {
                // For y = -1 and y = 1
                for (int y = -1; y < 2; y += 2) {
                    temp_row = row + x;
                    temp_col = col + y;
                    // Avoid going off the edge
                    while (temp_row > 0 && temp_row < 9 && temp_col > 0 && temp_col < 9) {
                        // If there is a piece in the way:
                        if (board.getPiece(new ChessPosition(temp_row, temp_col)) != null) {
                            // If the piece is an enemy, allow the move
                            if (board.getPiece(new ChessPosition(temp_row, temp_col)).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                                moves.add(new ChessMove(myPosition, new ChessPosition(temp_row, temp_col), null));
                            }
                            // Regardless, stop the loop (path is blocked)
                            break;
                        }
                        // Otherwise, add the move
                        else {
                            moves.add(new ChessMove(myPosition, new ChessPosition(temp_row, temp_col), null));
                            if (board.getPiece(new ChessPosition(temp_row, temp_col)) != null) break;
                            temp_row += x;
                            temp_col += y;
                        }
                    }
                }
            }
        }
        // King
        else if (type == PieceType.KING) {
            // For x = -1, 0, 1
            for (int x = -1; x < 2; x++) {
                // For y = -1, 0, 1
                for (int y = -1; y < 2; y++) {
                    int temp_row = row + x;
                    int temp_col = col + y;
                    // Avoid going off the edge
                    if (temp_row > 0 && temp_row < 9 && temp_col > 0 && temp_col < 9) {
                        // If space is empty or occupied by enemy
                        if ((board.getPiece(new ChessPosition(temp_row, temp_col)) == null) || (board.getPiece(new ChessPosition(temp_row, temp_col)).getTeamColor() != board.getPiece(myPosition).getTeamColor()))  {
                            moves.add(new ChessMove(myPosition, new ChessPosition(temp_row, temp_col), null));
                        }
                    }

                }
            }
        }
        // Knight
        else if (type == PieceType.KNIGHT) {
            // For x == -2,-1,0,1,2
            for (int x = -2; x < 3; x++) {
                // For y == -2,-1,0,1,2
                for (int y = -2; y < 3; y++) {
                    // If neither number is 0 and the difference is 1, then we have an L shape
                    if (x != 0 && y != 0 && abs(abs(x)-abs(y)) == 1) {
                        int temp_row = row + x;
                        int temp_col = col + y;
                        // If not out of bounds
                        if (temp_row > 0 && temp_row < 9 && temp_col > 0 && temp_col < 9) {
                            // If space is empty or occupied by enemy
                            if ((board.getPiece(new ChessPosition(temp_row, temp_col)) == null) || (board.getPiece(new ChessPosition(temp_row, temp_col)).getTeamColor() != board.getPiece(myPosition).getTeamColor()))  {
                                moves.add(new ChessMove(myPosition, new ChessPosition(temp_row, temp_col), null));
                            }
                        }
                    }
                }
            }
        }
        // Pawn
        else if (type == PieceType.PAWN) {
            // Movement direction is down for black, up for white
            int direction;
            if (board.getPiece(myPosition).getTeamColor() == ChessGame.TeamColor.BLACK) {
                direction = -1;
            } else {
                direction = 1;
            }
            // Whether or not the pawn will earn a promotion by moving forward
            boolean earnsPromotion = (((direction == -1 && row == 2) || (direction == 1 && row == 7)));
            // If the pawn is on the second row of its side, assume first move
            boolean firstMove = ((direction == -1 && row == 7) || (direction == 1 && row == 2));
            // Normal forward movement
            if ((board.getPiece(new ChessPosition(row + direction, col)) == null))  {
                moves.add(new ChessMove(myPosition, new ChessPosition(row + direction, col), null));
                // If the forward space was available AND it is the first move, allow double movement
                if (firstMove && (board.getPiece(new ChessPosition(row + (2 * direction), col)) == null))  {
                    moves.add(new ChessMove(myPosition, new ChessPosition(row + (2 * direction), col), null));
                }
            }
            // Diagonal left movement
            if (col > 1 && (board.getPiece(new ChessPosition(row + direction, col - 1)) != null) && (board.getPiece(new ChessPosition(row + direction, col - 1)).getTeamColor() != board.getPiece(myPosition).getTeamColor()))  {
                moves.add(new ChessMove(myPosition, new ChessPosition(row + direction, col - 1), null));
            }
            // Diagonal right movement
            if (col < 8 && (board.getPiece(new ChessPosition(row + direction, col + 1)) != null) && (board.getPiece(new ChessPosition(row + direction, col + 1)).getTeamColor() != board.getPiece(myPosition).getTeamColor()))  {
                moves.add(new ChessMove(myPosition, new ChessPosition(row + direction, col + 1), null));
            }
            // If the pawn will be promoted, replace each pawn move with four corresponding moves of different pieces
            if (earnsPromotion) {
                // Temporary array to store new moves
                ArrayList<ChessMove> tempMoves = new ArrayList<ChessMove>();
                for (ChessMove move : moves) {
                    tempMoves.add(new ChessMove(myPosition, move.getEndPosition(), PieceType.QUEEN));
                    tempMoves.add(new ChessMove(myPosition, move.getEndPosition(), PieceType.BISHOP));
                    tempMoves.add(new ChessMove(myPosition, move.getEndPosition(), PieceType.ROOK));
                    tempMoves.add(new ChessMove(myPosition, move.getEndPosition(), PieceType.KNIGHT));
                }
                // Replace pawn moves with other piece moves
                moves = tempMoves;
            }
        }
        // Queen
        else if (type == PieceType.QUEEN) {
            // Works the same as bishop, except allows x and y to be 0
            int temp_row;
            int temp_col;
            // For x = -1, 0, 1
            for (int x = -1; x < 2; x += 1) {
                // For x = -1, 0, 1
                for (int y = -1; y < 2; y += 1) {
                    temp_row = row + x;
                    temp_col = col + y;
                    // Avoid going off the edge
                    while (temp_row > 0 && temp_row < 9 && temp_col > 0 && temp_col < 9) {
                        // If there is a piece in the way:
                        if (board.getPiece(new ChessPosition(temp_row, temp_col)) != null) {
                            // If the piece is an enemy, allow the move
                            if (board.getPiece(new ChessPosition(temp_row, temp_col)).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                                moves.add(new ChessMove(myPosition, new ChessPosition(temp_row, temp_col), null));
                            }
                            // Regardless, stop the loop (path is blocked)
                            break;
                        }
                        // Otherwise, add the move
                        else {
                            moves.add(new ChessMove(myPosition, new ChessPosition(temp_row, temp_col), null));
                            System.out.println(temp_row+", "+temp_col);
                            if (board.getPiece(new ChessPosition(temp_row, temp_col)) != null) break;
                            temp_row += x;
                            temp_col += y;
                        }
                    }
                }
            }
        }
        // Rook
        else if (type == PieceType.ROOK) {
            int temp_row;
            int temp_col;
            int[] xList = {-1,1,0,0};
            int[] yList = {0,0,-1,1};
            // Loops through pairs:
            // (-1,0), (1,0), (0,-1), (0,1)
            for (int i = 0; i < 4; i++) {
                int x = xList[i];
                int y = yList[i];
                temp_row = row + x;
                temp_col = col + y;
                // Avoid going off the edge
                while (temp_row > 0 && temp_row < 9 && temp_col > 0 && temp_col < 9) {
                    // If there is a piece in the way:
                    if (board.getPiece(new ChessPosition(temp_row, temp_col)) != null) {
                        // If the piece is an enemy, allow the move
                        if (board.getPiece(new ChessPosition(temp_row, temp_col)).getTeamColor() != board.getPiece(myPosition).getTeamColor()) {
                            moves.add(new ChessMove(myPosition, new ChessPosition(temp_row, temp_col), null));
                        }
                        // Regardless, stop the loop (path is blocked)
                        break;
                    }
                    // Otherwise, add the move
                    else {
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
