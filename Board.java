import javax.imageio.ImageIO;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.image.*;
import java.util.concurrent.ConcurrentHashMap;

class Board {
    private int turn;
    private ConcurrentHashMap<Integer, Integer> pieces;
    private HashMap<Integer, String> pieceNames = new HashMap<>();
    private HashMap<Integer, BufferedImage> pieceSprites = new HashMap<>();
    private HashMap<Integer, Double> pieceValues = new HashMap<>();
    private HashSet<Integer> moveList = new HashSet<>();
    private boolean castleKW = true;
    private boolean castleQW = true;
    private boolean castleKB = true;
    private boolean castleQB = true;
    Board (int turn, HashMap<Integer, Integer> pieces) {
        this.turn = turn;
        this.pieces = new ConcurrentHashMap<>(pieces);
        pieceNames.put(6, "k");
        pieceNames.put(5, "q");
        pieceNames.put(4, "r");
        pieceNames.put(3, "n");
        pieceNames.put(2, "b");
        pieceNames.put(1, "p");

        pieceValues.put(6, 999.0);
        pieceValues.put(5, 9.0);
        pieceValues.put(4, 5.0);
        pieceValues.put(3, 3.0);
        pieceValues.put(2, 3.0);
        pieceValues.put(1, 1.0);

        try {
            for (Integer piece : pieceNames.keySet()) {
                pieceSprites.put(piece, ImageIO.read(new File(piece + ".png")));
                pieceSprites.put(-piece, ImageIO.read(new File(-piece + ".png")));
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
    public void changeTurn() {
        this.turn = turn * -1;
    }
    public int pieceColor(int position) {
        if (pieces.get(position) > 0) {
            return 1;
        } else {
            return -1;
        }
    }
    public Integer getPiece(int position) {
        return this.pieces.get(position);
    }
    public boolean emptySquare(int position) { // Returns true if the square is empty
        return !this.pieces.containsKey(position);
    }
    public boolean friendlySquare(int position, int color) { // Returns true if the square is occupied by a friendly piece
        return (!emptySquare(position) && pieceColor(position) == color);
    }
    public void changePiecePos(int oldPosition, int newPosition) {
        pieces.put(newPosition, pieces.remove(oldPosition));
    }
    public int movePiece(int oldPosition, int newPosition) {
        if (Math.abs(pieces.get(oldPosition)) == 6) { // CHECK IF INTEGER != INT
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
        return removedPiece;
    }
    public void castlePiece(int oldKingPos, int newKingPos, int oldRookPos, int newRookPos) {
        changePiecePos(oldKingPos, newKingPos);
        try {
            changePiecePos(oldRookPos, newRookPos);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println(oldRookPos + " " + newRookPos);
            System.out.println(pieces.toString());
            System.out.println(toFEN());
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
        if (capturedPiece != 0) {
            pieces.put(newPosition, capturedPiece);
        }
    }
    public boolean isCheckmate() {
        int numOfLegalMoves = 0;
        for (Integer position : pieces.keySet()) {
            if (pieceColor(position) == turn) {
                numOfLegalMoves += findLegalMoves(position).size();
            }
        }
        return numOfLegalMoves == 0;
    }
    public void checkPromotion() {
        for (Integer position : pieces.keySet()) {
            if (pieces.get(position) == 1 && position % 10 == 1) { // White pawn
                pieces.remove(position);
                pieces.put(position, 5);
            } else if (pieces.get(position) == -1 && position % 10 == 8) { // Black pawn
                pieces.remove(position);
                pieces.put(position, -5);
            }
        }
    }
    public boolean getCastleKB() {
        return castleKB;
    }
    public boolean getCastleKW() {
        return castleKW;
    }
    public boolean getCastleQB() {
        return castleQB;
    }
    public boolean getCastleQW() {
        return castleQW;
    }
    public void setCastleKW(boolean bool) {
        castleKW = bool;
    }
    public void setCastleQW(boolean bool) {
        castleQW = bool;
    }
    public void setCastleKB(boolean bool) {
        castleKB = bool;
    }
    public void setCastleQB(boolean bool) {
        castleQB = bool;
    }


    // Piece Movement
    public void explore(int position, int direction, int color, boolean range, HashSet<Integer> moves) {
        position = position + direction;
        if (position / 10 < 9 && position / 10 > 0 && position % 10 > 0 && position % 10 < 9) {
            moves.add(position);
            if (emptySquare(position) && range) {
                explore(position, direction, color, range, moves);
            }
        }
    }
    public void cardinalMoves(int position, boolean range, HashSet<Integer> moves) {
        explore(position, -1, pieceColor(position), range, moves); // N
        explore(position, 1, pieceColor(position), range, moves); // S
        explore(position, -10, pieceColor(position), range, moves); // W
        explore(position, 10, pieceColor(position), range, moves); // E
    }
    public void diagonalMoves(int position, boolean range, HashSet<Integer> moves) {
        explore(position, -11, pieceColor(position), range, moves); // NW
        explore(position, 9, pieceColor(position), range, moves); // NE
        explore(position, -9, pieceColor(position), range, moves); // SW
        explore(position, 11, pieceColor(position), range, moves); // SE
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
            if (emptySquare(position - 1)) {
                moves.add(position - 1);
            }
            if (position % 10 == 7 && emptySquare(position - 1) && emptySquare(position - 2)) {
                moves.add(position - 2);
            }
        } else {
            if (emptySquare(position + 1)) {
                moveList.add(position + 1);
            }
            if (position % 10 == 2 && emptySquare(position + 1) && emptySquare(position + 2)) {
                moveList.add(position + 2);
            }
        }
    }
    public void knightMoves(int position, HashSet<Integer> moves) {
        int[] knightSquares = { -8, 12, 21, 19, 8, -12, -21, -19};
        for (int direction : knightSquares) {
            explore(position, direction, pieceColor(position), false, moves);
        }
    }
    public void findPossibleMoves(int position, HashSet<Integer> moves) {
        int piece = Math.abs(pieces.get(position));
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
        HashSet<Integer> possibleMoveList = new HashSet<>();
        for (Integer position : pieces.keySet()) {
            if (pieceColor(position) == color) {
                findPossibleMoves(position, possibleMoveList);
            }
        }
        return possibleMoveList;
    }
    public int findKingPos(int color) {
        for (Integer position : pieces.keySet()) {
            if (pieces.get(position) == 6 * color) {
                return position;
            }
        }
        return -1;
    }
    public boolean validRook(int position, int color) {
        return friendlySquare(position, color) && pieces.get(position) == 4 * color;
    }
    public void kingCastleMoves(int position, HashSet<Integer> moves) {
        if (pieceColor(position) > 0) {
            if (castleKW && emptySquare(68) && emptySquare(78)
            && !findAllPossibleMoves(-1).contains(68) && validRook(88, 1)) {
                moves.add(78);
            }
            if (castleQW && emptySquare(48) && emptySquare(38)
                    && !findAllPossibleMoves(-1).contains(48) && validRook(18, 1)) {
                moves.add(38);
            }
        } else if (pieceColor(position) < 0) {
            if (castleKB && emptySquare(61) && emptySquare(71)
                    && !findAllPossibleMoves(1).contains(61) && validRook(18, -1)) {
                moves.add(71);
            }
            if (castleKW && emptySquare(41) && emptySquare(31)
                    && !findAllPossibleMoves(1).contains(41) && validRook(11, -1)) {
                moves.add(31);
            }
        }
    }
    public HashSet<Integer> findLegalMoves(int position) {
        moveList.clear();
        if (Math.abs(pieces.get(position)) == 1) {
            pawnForwardMoves(position, moveList);
            if (pieceColor(position) > 0) {
                if (position / 10 > 1 && !emptySquare(position - 11)) {
                    moveList.add(position - 11);
                }
                if (position / 10 < 8 && !emptySquare(position + 9)) {
                    moveList.add(position + 9);
                }
            } else if (pieceColor(position) < 0) {
                if (position / 10 > 1 && !emptySquare(position - 9)) {
                    moveList.add(position - 9);
                }
                if (position / 10 < 8 && !emptySquare(position + 11)) {
                    moveList.add(position + 11);
                }
            }
        } else {
            findPossibleMoves(position, moveList);
        }
        if (Math.abs(pieces.get(position)) == 6) {
            kingCastleMoves(position, moveList);
        }
        HashSet<Integer> legalMoves = new HashSet<>();
        for (Integer newPosition : moveList) {
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

    // Graphics
    public String toFEN() {
        String[][] chessboard = new String[8][8];
        for (Integer position : pieces.keySet()) {
            String pieceCharacter = pieceNames.get(pieces.get(position));
            if (pieceColor(position) > 0) {
                pieceCharacter = pieceCharacter.toUpperCase();
            }
            chessboard[position % 10 - 1][position / 10 - 1] = pieceCharacter;
        }
        String fen = "";
        for (int i = 0; i < chessboard.length; i++) {
            String[] row = chessboard[i];
            int counter = 0;
            for (int j = 0; j < row.length; j++) {
                if (row[j] == null) {
                    counter++;
                } else if (counter == 0) {
                    fen += row[j];
                } else {
                    fen += counter + row[j];
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
    public void drawEvaluation(Graphics g, int GRIDSIZE) {
        Color whiteGrey = new Color(200, 200, 200);
        Color blackGrey = new Color(100, 100, 100);
        double evaluation = evaluateBoard();
        int evaluationDisplay = (int)(-evaluation * 20);
        // Black Gauge
        g.setColor(blackGrey);
        g.fillRect(GRIDSIZE * 8, 0, 20, evaluationDisplay + GRIDSIZE * 4);
        // White Gauge
        g.setColor(whiteGrey);
        g.fillRect(GRIDSIZE * 8, evaluationDisplay + GRIDSIZE * 4, 20, GRIDSIZE * 4 - evaluationDisplay);
    }
    public void drawBoard(Graphics g, int GRIDSIZE) {
        Color lightSquare = new Color(241, 217, 182);
        Color darkSquare = new Color(181, 137, 99);
        Color highlightedLightSquare = new Color(205, 210, 106);
        Color highlightedDarkSquare = new Color(170, 162, 58);
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if ((x + y) % 2 == 0) {
                    if (Chess.selectedSquares.contains(10 * (x + 1) + y + 1)) {
                        g.setColor(highlightedLightSquare);
                    } else {
                        g.setColor(lightSquare);
                    }
                } else {
                    if (Chess.selectedSquares.contains(10 * (x + 1) + y + 1)) {
                        g.setColor(highlightedDarkSquare);
                    } else {
                        g.setColor(darkSquare);
                    }
                }
                g.fillRect(x * GRIDSIZE, y * GRIDSIZE, GRIDSIZE, GRIDSIZE);
            }
        }

        for (Integer position : pieces.keySet()) {
            g.drawImage(pieceSprites.get(pieces.get(position)), (position / 10 - 1) * GRIDSIZE, (position % 10 - 1) * GRIDSIZE, null);
        }
    }

    // Logic Methods
    public double evaluateBoard() {
        double evaluation = 0;
        double developmentBoost = 0.1;
        double pieceActivity = 0.02;
        for (Integer position : pieces.keySet()) {
            evaluation += pieceValues.get(Math.abs(pieces.get(position))) * pieceColor(position);
        }
        return evaluation;
    }
}
