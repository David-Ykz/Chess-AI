import jdk.jfr.StackTrace;

import javax.imageio.ImageIO;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.image.*;
import java.util.concurrent.ConcurrentHashMap;

class Board {
    public String[] pieceNames = {"p", "b", "n", "r", "q", "k"};
    public double[] pieceValues = {1.0, 3.0, 3.0, 5.0, 9.0, 200.0};
    public HashMap<Integer, BufferedImage> pieceSprites = new HashMap<>();
    private int turn;
    private int playerColor;
    private ConcurrentHashMap<Integer, Integer> pieces;
    private boolean[] validRook = {true, true, true, true}; // QB KB QW KW
    private boolean[] validKing = {true, true}; // B W
    private boolean[] aiValidRook = {true, true, true, true}; // QB KB QW KW
    private boolean[] aiValidKing = {true, true}; // B W
    EvaluationMaps evalMap = new EvaluationMaps();
    private Stack<Move> boardMoves = new Stack<>();
    private HashSet<Integer> piecesMoved = new HashSet<>();

    Board (int turn, HashMap<Integer, Integer> pieces, int playerColor) {
        this.turn = turn;
        this.pieces = new ConcurrentHashMap<>(pieces);
        this.playerColor = playerColor;
        // Loads all sprites
        try {
            for (int i = 1; i <= 6; i++) {
                pieceSprites.put(i, ImageIO.read(new File(i + ".png")));
                pieceSprites.put(-i, ImageIO.read(new File(-i + ".png")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Helper Methods
    public ConcurrentHashMap<Integer, Integer> getPieces() {
        return pieces;
    }
    public int getTurn() {
        return this.turn;
    }
    public int getPlayerColor() {
        return this.playerColor;
    }
    public int pieceColor(int position) {
        if (pieces.get(position) > 0) {
            return 1;
        } else {
            return -1;
        }
    }
    public boolean emptySquare(int position) { // Returns true if the square is empty
        return !this.pieces.containsKey(position);
    }
    public boolean friendlySquare(int position, int color) { // Returns true if the square is occupied by a friendly piece
        return (!emptySquare(position) && pieceColor(position) == color);
    }
    public boolean isCheckmate() {
        int numOfLegalMoves = 0;
        HashSet<Integer> positions = new HashSet<>(pieces.keySet());
        for (Integer position : positions) {
            if (pieceColor(position) == turn) {
                numOfLegalMoves += findLegalMoves(position).size();
            }
        }
        return numOfLegalMoves == 0;
    }
    public boolean checkPromotion(int oldPosition, int newPosition) {
        if (Math.abs(pieces.get(oldPosition)) != 1) {
            return false;
        }
        if (newPosition % 10 == 1 || newPosition % 10 == 8) {
            return true;
        }
        return false;
    }

    // Alter board state
    public void changePiecePos(int oldPosition, int newPosition) {
        pieces.put(newPosition, pieces.remove(oldPosition));
    }
    public int movePiece(Move move) {
        if (move.getPromotedPiece() != 0) {
            promotePawn(move.getOldPosition(), move.getPromotedPiece());
        }
        return movePiece(move.getOldPosition(), move.getNewPosition());
    }
    public int movePiece(int oldPosition, int newPosition) {
        if (Math.abs(pieces.get(oldPosition)) == 6) {
            if (oldPosition == 58) {
                if (newPosition == 38) { // Queenside castle
                    castlePiece(oldPosition, newPosition, 18, newPosition + 10);
                    return 0;
                } else if (newPosition == 78) { // Kingside castle
                    castlePiece(oldPosition, newPosition, 88, newPosition - 10);
                    return 0;
                }
            } else if (oldPosition == 51) {
                if (newPosition == 31) { // Queenside castle
                    castlePiece(oldPosition, newPosition, 11, newPosition + 10);
                    return 0;
                } else if (newPosition == 71) { // Kingside castle
                    castlePiece(oldPosition, newPosition, 81, newPosition - 10);
                    return 0;
                }
            }
        }
        int removedPiece = 0;
        if (pieces.containsKey(newPosition) && pieceColor(oldPosition) != pieceColor(newPosition)) {
            removedPiece = pieces.remove(newPosition);
        }
        changePiecePos(oldPosition, newPosition);
        updateRookStatus(aiValidRook, oldPosition, newPosition, pieces.get(newPosition) % 10, removedPiece);
        updateKingStatus(aiValidKing, pieces.get(newPosition) % 10);
        return removedPiece;
    }
    public void makeMove(Move move) {
        int piece = pieces.get(move.getOldPosition()) % 10;
        int capturedPiece = movePiece(move);
        boardMoves.add(new Move(move, piece, capturedPiece));
        piecesMoved.add(piece);
        updateRookStatus(validRook, move.getOldPosition(), move.getNewPosition(), piece, capturedPiece);
        updateKingStatus(validKing, piece);
        turn = turn * -1;
        if (Math.abs(pieces.get(move.getNewPosition())) > 10) {
            pieces.put(move.getNewPosition(), pieces.remove(move.getNewPosition()) % 10);
        }
    }
    public void revertMove(int oldPosition, int newPosition, int capturedPiece) {
        if (Math.abs(pieces.get(newPosition)) == 6) { // CHECK IF INTEGER != INT
            if (oldPosition == 51) { // Black king
                if (newPosition == 31) {
                    changePiecePos(41, 11); // Queenside castle
                } else if (newPosition == 71) {
                    changePiecePos(61, 81); // Kingside castle
                }
            }
            if (oldPosition == 58) { // White king
                if (newPosition == 38) {
                    changePiecePos(48, 18); // Queenside castle
                } else if (newPosition == 78) {
                    changePiecePos(68, 88); // Kingside castle
                }
            }
        }
        changePiecePos(newPosition, oldPosition);
        if (pieces.get(oldPosition) > 10) {
            pieces.put(oldPosition, 1);
        } else if (pieces.get(oldPosition) < -10) {
            pieces.put(oldPosition, -1);
        }
        if (capturedPiece != 0) {
            pieces.put(newPosition, capturedPiece);
        }
        if (pieces.get(oldPosition) == -4 && oldPosition == 11) {
            aiValidRook[0] = true;
        } else if (pieces.get(oldPosition) == -4 && oldPosition == 81) {
            aiValidRook[1] = true;
        } else if (pieces.get(oldPosition) == 4 && oldPosition == 18) {
            aiValidRook[2] = true;
        } else if (pieces.get(oldPosition) == 4 && oldPosition == 88) {
            aiValidRook[3] = true;
        }
        if (!aiValidKing[0] && pieces.get(oldPosition) == -6 && oldPosition == 51) {
            aiValidKing[0] = true;
        } else if (!aiValidKing[1] && pieces.get(oldPosition) == 6 && oldPosition == 58) {
            aiValidKing[1] = true;
        }
        if (capturedPiece == -4 && newPosition == 11) {
            aiValidRook[0] = true;
        } else if (capturedPiece == -4 && newPosition == 81) {
            aiValidRook[1] = true;
        } else if (capturedPiece == 4 && newPosition == 18) {
            aiValidRook[2] = true;
        } else if (capturedPiece == 4 && newPosition == 88) {
            aiValidRook[3] = true;
        }
    }
    public void promotePawn(int position, int piece) {
        pieces.remove(position);
        pieces.put(position, piece);
    }

    // Castling
    public void castlePiece(int oldKingPos, int newKingPos, int oldRookPos, int newRookPos) {
        changePiecePos(oldKingPos, newKingPos);
        changePiecePos(oldRookPos, newRookPos);
    }
    public void updateRookStatus(boolean[] array, int oldPos, int newPos, int piece, int capturedPiece) {
        if (piece == -4) { // Black rook moved
            if (oldPos == 11) {
                array[0] = false; // QB
            } else if (oldPos == 81) {
                array[1] = false; // KB
            }
        } else if (piece == 4) { // White rook moved
            if (oldPos == 18) {
                array[2] = false; // QW
            } else if (oldPos == 88) {
                array[3] = false; // KW
            }
        }
        if (capturedPiece == -4) { // Black rook captured
            if (newPos == 11) {
                array[0] = false; // QB
            } else if (newPos == 81) {
                array[1] = false; // KB
            }
        } else if (capturedPiece == 4) { // White rook captured
            if (newPos == 18) {
                array[2] = false; // QW
            } else if (newPos == 88) {
                array[3] = false; // KW
            }
        }
    }
    public void updateKingStatus(boolean[] array, int piece) {
        if (piece == -6) { // Black king moved
            array[0] = false; // B
        } else if (piece == 6) { // White king moved
            array[1] = false; // W
        }
    }
    public boolean validRook(int position) {
        if (position == 11) { // Black queenside rook
            return validRook[0] && aiValidRook[0] && pieces.containsKey(11) && pieces.get(11) == -4;
        } else if (position == 81) {
            return validRook[1] && aiValidRook[1] && pieces.containsKey(81) && pieces.get(81) == -4;
        } else if (position == 18) {
            return validRook[2] && aiValidRook[2] && pieces.containsKey(18) && pieces.get(18) == 4;
        } else {
            return validRook[3] && aiValidRook[3] && pieces.containsKey(88) && pieces.get(88) == 4;
        }
    }
    public void kingCastleMoves(int position, HashSet<Integer> moves) {
        // Black king hasn't moved and isn't in check
        if (pieceColor(position) < 0 && validKing[0] && aiValidKing[0] && !findAllPossibleMoves(1).contains(51)) {
            // Queenside castle
            HashSet<Integer> allWhiteSquares = findAllPossibleMoves(1);
            if (validRook(11) && emptySquare(41) && emptySquare(31) && emptySquare(21)
            && !allWhiteSquares.contains(41) && !allWhiteSquares.contains(31)) {
                moves.add(31);
            }
            // Kingside castle
            if (validRook(81) && emptySquare(61) && emptySquare(71)
            && !allWhiteSquares.contains(61)) {
                moves.add(71);
            }
            // White king hasn't moved and isn't in check
        } else if (pieceColor(position) > 0 && validKing[1] && aiValidKing[1] && !findAllPossibleMoves(-1).contains(58)) {
            HashSet<Integer> allBlackSquares = findAllPossibleMoves(-1);
            // Queenside castle
            if (validRook(18) && emptySquare(48) && emptySquare(38) && emptySquare(28)
            && !allBlackSquares.contains(48) && !allBlackSquares.contains(38)) {
                moves.add(38);
            }
            // Kingside castle
            if (validRook(88) && emptySquare(68) && emptySquare(78)
            && !allBlackSquares.contains(68)) {
                moves.add(78);
            }
        }
    }

    // Piece Movement
    public void explore(int position, int direction, boolean range, HashSet<Integer> moves) {
        position = position + direction;
        if (position / 10 < 9 && position / 10 > 0 && position % 10 > 0 && position % 10 < 9) {
            moves.add(position);
            if (emptySquare(position) && range) {
                explore(position, direction, range, moves);
            }
        }
    }
    public void cardinalMoves(int position, boolean range, HashSet<Integer> moves) {
        explore(position, -1, range, moves); // N
        explore(position, 1, range, moves); // S
        explore(position, -10, range, moves); // W
        explore(position, 10, range, moves); // E
    }
    public void diagonalMoves(int position, boolean range, HashSet<Integer> moves) {
        explore(position, -11, range, moves); // NW
        explore(position, 9, range, moves); // NE
        explore(position, -9, range, moves); // SW
        explore(position, 11, range, moves); // SE
    }
    public void pawnCaptureMoves(int position, HashSet<Integer> moves) {
        if (pieceColor(position) > 0) {
            if (position / 10 > 1) {
                moves.add(position - 11);
            }
            if (position / 10 < 8) {
                moves.add(position + 9);
            }
        } else if (pieceColor(position) < 0) {
            if (position / 10 > 1) {
                moves.add(position - 9);
            }
            if (position / 10 < 8) {
                moves.add(position + 11);
            }
        }
    }
    public void pawnForwardMoves(int position, HashSet<Integer> moves) {
        if (pieceColor(position) > 0) {
            if (emptySquare(position - 1) && position % 10 > 1) {
                moves.add(position - 1);
            }
            if (position % 10 == 7 && emptySquare(position - 1) && emptySquare(position - 2)) {
                moves.add(position - 2);
            }
        } else {
            if (emptySquare(position + 1) && position % 10 < 8) {
                moves.add(position + 1);
            }
            if (position % 10 == 2 && emptySquare(position + 1) && emptySquare(position + 2)) {
                moves.add(position + 2);
            }
        }
    }
    public void knightMoves(int position, HashSet<Integer> moves) {
        int[] knightSquares = { -8, 12, 21, 19, 8, -12, -21, -19};
        for (int direction : knightSquares) {
            explore(position, direction, false, moves);
        }
    }

    // Move generation
    public void findPossibleMoves(int position, HashSet<Integer> moves) {
        int piece = Math.abs(pieces.get(position)) % 10;
        if (piece == 1) { // Pawn
            pawnCaptureMoves(position, moves);
        } else if (piece == 2) {
            diagonalMoves(position, true, moves);
        } else if (piece == 3) {
            knightMoves(position, moves);
        } else if (piece == 4) {
            cardinalMoves(position, true, moves);
        } else if (piece == 5) {
            cardinalMoves(position, true, moves);
            diagonalMoves(position, true, moves);
        } else if (piece == 6) {
            cardinalMoves(position, false, moves);
            diagonalMoves(position, false, moves);
        }
    }
    public HashSet<Integer> findAllPossibleMoves(int color) {
        HashSet<Integer> moves = new HashSet<>();
        for (Integer position : pieces.keySet()) {
            if (pieceColor(position) == color) {
                findPossibleMoves(position, moves);
            }
        }
        return moves;
    }
    public int findKingPos(int color) {
        for (Integer position : pieces.keySet()) {
            if (pieces.get(position) == 6 * color) {
                return position;
            }
        }
        return -1;
    }
    public HashSet<Integer> findLegalMoves(int position) {
        HashSet<Integer> moves = new HashSet<>();
        if (Math.abs(pieces.get(position)) == 1) {
            pawnForwardMoves(position, moves);
            if (pieceColor(position) > 0) {
                if (position / 10 > 1 && !emptySquare(position - 11)) {
                    moves.add(position - 11);
                }
                if (position / 10 < 8 && !emptySquare(position + 9)) {
                    moves.add(position + 9);
                }
            } else if (pieceColor(position) < 0) {
                if (position / 10 > 1 && !emptySquare(position - 9)) {
                    moves.add(position - 9);
                }
                if (position / 10 < 8 && !emptySquare(position + 11)) {
                    moves.add(position + 11);
                }
            }
        } else {
            findPossibleMoves(position, moves);
        }
        if (Math.abs(pieces.get(position)) == 6) {
            kingCastleMoves(position, moves);
        }
        HashSet<Integer> legalMoves = new HashSet<>();
        for (Integer newPosition : moves) {
            if (!friendlySquare(newPosition, pieceColor(position))) {
                int capturedPiece = movePiece(position, newPosition);
                if (!findAllPossibleMoves(-1 * pieceColor(newPosition)).contains(findKingPos(pieceColor(newPosition)))) {
                    legalMoves.add(newPosition);
                }
                revertMove(position, newPosition, capturedPiece);
            }
        }
        return legalMoves;
    }
    public HashSet<Integer> findCaptureMoves(int position) {
        HashSet<Integer> moves = new HashSet<>();
        if (Math.abs(pieces.get(position)) == 1) {
            if (pieceColor(position) > 0) {
                if (position / 10 > 1 && !emptySquare(position - 11)) {
                    moves.add(position - 11);
                }
                if (position / 10 < 8 && !emptySquare(position + 9)) {
                    moves.add(position + 9);
                }
            } else if (pieceColor(position) < 0) {
                if (position / 10 > 1 && !emptySquare(position - 9)) {
                    moves.add(position - 9);
                }
                if (position / 10 < 8 && !emptySquare(position + 11)) {
                    moves.add(position + 11);
                }
            }
        } else {
            findPossibleMoves(position, moves);
        }
        HashSet<Integer> legalMoves = new HashSet<>();
        for (Integer newPosition : moves) {
            if (!friendlySquare(newPosition, pieceColor(position))) {
                int capturedPiece = movePiece(position, newPosition);
                if (capturedPiece != 0 && !findAllPossibleMoves(-1 * pieceColor(newPosition)).contains(findKingPos(pieceColor(newPosition)))) {
                    legalMoves.add(newPosition);
                }
                revertMove(position, newPosition, capturedPiece);
            }
        }
        return legalMoves;
    }
    public HashSet<Move> allLegalMoves(int color) {
        HashSet<Move> moves = new HashSet<>();
        HashSet<Integer> piecePositions = new HashSet<>(pieces.keySet());
        for (Integer oldPosition : piecePositions) {
            if (pieceColor(oldPosition) == color) { // Finds all pieces of the turn player
                for (int eachMove : findLegalMoves(oldPosition)) {
                    if (checkPromotion(oldPosition, eachMove)) {
                        for (int i = 2; i < 6; i++) {
                            Move move = new Move(oldPosition, eachMove, (i + 10) * color);
                            moves.add(move);
                        }
                    } else {
                        moves.add(new Move(oldPosition, eachMove));
                    }
                }
            }
        }
        return moves;
    }
    public HashSet<Move> allCaptureMoves(int color) {
        HashSet<Move> moves = new HashSet<>();
        HashSet<Integer> piecePositions = new HashSet<>(getPieces().keySet());
        for (Integer oldPosition : piecePositions) {
            if (pieceColor(oldPosition) == color) { // Finds all pieces of the turn player
                for (int eachMove : findCaptureMoves(oldPosition)) {
                    moves.add(new Move(oldPosition, eachMove));
                }
            }
        }
        return moves;
    }
    public HashSet<Integer> allPawnSquares(int color) {
        HashSet<Integer> moves = new HashSet<>();
        for (Integer position : pieces.keySet()) {
            int piece = pieces.get(position);
            if (piece == color) {
                pawnCaptureMoves(position, moves);
            }
        }
        return moves;
    }
    
    // Graphics
    public String toFEN() {
        String[][] chessboard = new String[8][8];
        for (Integer position : pieces.keySet()) {
            String pieceCharacter = pieceNames[Math.abs(pieces.get(position)) % 10 - 1];
            if (pieceColor(position) > 0) {
                pieceCharacter = pieceCharacter.toUpperCase();
            }
            chessboard[position % 10 - 1][position / 10 - 1] = pieceCharacter;
        }
        String fen = "";
        for (String[] row : chessboard) {
            int counter = 0;
            for (String s : row) {
                if (s == null) {
                    counter++;
                } else if (counter == 0) {
                    fen += s;
                } else {
                    fen += counter + s;
                    counter = 0;
                }
            }
            if (counter != 0) {
                fen += counter;
            }
            fen += "/";
        }
        fen = fen.substring(0, fen.length() - 1);
        if (this.turn > 0) {
            fen += " b";
        } else {
            fen += " w";
        }
        return fen;
    }
    public void drawBoard(Graphics g, int GRIDSIZE) {
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if ((x + y) % 2 == 0) {
                    if (Chess.selectedSquares.contains(10 * (x + 1) + y + 1)) {
                        g.setColor(Colors.highlightedLightSquare);
                    } else {
                        g.setColor(Colors.lightSquare);
                    }
                } else {
                    if (Chess.selectedSquares.contains(10 * (x + 1) + y + 1)) {
                        g.setColor(Colors.highlightedDarkSquare);
                    } else {
                        g.setColor(Colors.darkSquare);
                    }
                }
                g.fillRect(x * GRIDSIZE, y * GRIDSIZE, GRIDSIZE, GRIDSIZE);
            }
        }
        for (Integer position : pieces.keySet()) {
            if (playerColor > 0) {
                g.drawImage(pieceSprites.get(pieces.get(position) % 10), (position / 10 - 1) * GRIDSIZE, (position % 10 - 1) * GRIDSIZE, null);
            } else {
                g.drawImage(pieceSprites.get(pieces.get(position) % 10), (8 - position / 10) * GRIDSIZE, (8 - position % 10) * GRIDSIZE, null);
            }
        }
    }

    // Evaluation Methods
    public double basicEvaluation() {
        double evaluation = 0;
        for (Integer position : pieces.keySet()) {
            evaluation += pieceValues[Math.abs(pieces.get(position) % 10) - 1] * pieceColor(position);
        }
        return evaluation;
    }
    public double openingEvaluation() {
        double evaluation = 0;
        for (Integer position : pieces.keySet()) {
            evaluation += evalMap.openingEvaluation(position, pieces.get(position) % 10);
        }
        return evaluation;
    }
    public double endgameEvaluation() {
        double evaluation = 0;
        if (basicEvaluation() > 2) {
            evaluation += 4.7 * evalMap.findCMD(findKingPos(-1));
            evaluation += 1.6 * (14 - evalMap.findMD(findKingPos(1), findKingPos(-1)));
        } else if (basicEvaluation() < -2) {
            evaluation -= 4.7 * evalMap.findCMD(findKingPos(1));
            evaluation -= 1.6 * (14 - evalMap.findMD(findKingPos(1), findKingPos(-1)));
        }
        return 0.1 * evaluation;
    }
    public double evaluateBoard() {
        double evaluation = 0;
        evaluation += basicEvaluation();
        if (pieces.size() > 5) {
            evaluation += openingEvaluation();
        } else {
            evaluation += endgameEvaluation();
        }
        return evaluation;
    }
}
