import javax.imageio.ImageIO;
import java.awt.*;
import java.util.*;
import java.io.*;
import java.awt.image.*;

class Board {
    private int turn;
    private HashMap<Integer, Integer> pieces;
    private HashMap<Integer, String> pieceNames = new HashMap<>();
    private HashMap<Integer, BufferedImage> pieceSprites = new HashMap<>();
    private HashMap<Integer, Double> pieceValues = new HashMap<>();
    private HashSet<Integer> moveList = new HashSet<>();
    Board (int turn, HashMap<Integer, Integer> pieces) {
        this.turn = turn;
        this.pieces = pieces;
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
    public HashMap<Integer, Integer> getPieces() {
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
    public boolean hostileSquare(int position, int color) {
        return (!emptySquare(position) && pieceColor(position) != color);
    }
    public void changePiecePos(int oldPosition, int newPosition) {
        pieces.put(newPosition, pieces.remove(oldPosition));
    }
    public int movePiece(int oldPosition, int newPosition) {
        if (pieces.get(oldPosition) == 6) { // CHECK IF INTEGER != INT
            if (oldPosition == 58) {
                if (newPosition == 38) { // Queenside castle
                    castlePiece(oldPosition, newPosition, pieces.get(18), newPosition + 10);
                    return 0;
                } else if (newPosition == 78) { // Kingside castle
                    castlePiece(oldPosition, newPosition, pieces.get(88), newPosition - 10);
                    return 0;
                }
            } else if (oldPosition == 51) {
                if (newPosition == 31) { // Queenside castle
                    castlePiece(oldPosition, newPosition, pieces.get(11), newPosition + 10);
                    return 0;
                } else if (newPosition == 71) { // Kingside castle
                    castlePiece(oldPosition, newPosition, pieces.get(81), newPosition - 10);
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
        changePiecePos(oldRookPos, newRookPos);
    }
    public void revertMove(int oldPosition, int newPosition, int capturedPiece) {
        if (pieces.get(newPosition) == 6) { // CHECK IF INTEGER != INT
            if (oldPosition == 51) { // Black king
                if (newPosition == 31) {
                    changePiecePos(pieces.get(41), 11); // Queenside castle
                } else if (newPosition == 71) {
                    changePiecePos(pieces.get(61), 81); // Kingside castle
                }
            }
            if (oldPosition == 58) { // White king
                if (newPosition == 38) {
                    changePiecePos(pieces.get(48), 18); // Queenside castle
                } else if (newPosition == 78) {
                    changePiecePos(pieces.get(68), 88); // Kingside castle
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

    // Piece Movement
    public void explore(int position, int direction, int color, boolean range) {
        position = position + direction;
        if (position / 10 < 9 && position / 10 > 0 && position % 10 > 0 && position % 10 < 9) {
            moveList.add(position);
            if (emptySquare(position) && range) {
                explore(position, direction, color, range);
            }
        }
    }
    public void cardinalMoves(int position, boolean range) {
        explore(position, -1, pieceColor(position), range); // N
        explore(position, 1, pieceColor(position), range); // S
        explore(position, -10, pieceColor(position), range); // W
        explore(position, 10, pieceColor(position), range); // E
    }
    public void diagonalMoves(int position, boolean range) {
        explore(position, -11, pieceColor(position), range); // NW
        explore(position, 9, pieceColor(position), range); // NE
        explore(position, -9, pieceColor(position), range); // SW
        explore(position, 11, pieceColor(position), range); // SE
    }
    public void pawnCaptureMoves(int position) {
        if (pieceColor(position) > 0) {
            if (position / 10 > 1) {
                moveList.add(position - 11);
            }
            if (position / 10 < 8) {
                moveList.add(position + 9);
            }
        } else if (pieceColor(position) < 0) {
            if (position / 10 > 1) {
                moveList.add(position - 9);
            }
            if (position / 10 < 8) {
                moveList.add(position + 11);
            }
        }
    }
    public void pawnForwardMoves(int position) {
        if (pieceColor(position) > 0) {
            if (emptySquare(position - 1)) {
                moveList.add(position - 1);
            }
            if (position % 10 == 7 && emptySquare(position - 1) && emptySquare(position - 2)) {
                moveList.add(position - 2);
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
    public void knightMoves(int position) {
        int[] knightSquares = { -8, 12, 21, 19, 8, -12, -21, -19};
        for (int direction : knightSquares) {
            explore(position, direction, pieceColor(position), false);
        }
    }
    public void findPossibleMoves(int position) {
        int piece = Math.abs(pieces.get(position));
        if (piece == 1) { // Pawn
            pawnCaptureMoves(position);
        } else if (piece == 2) {
            diagonalMoves(position, true);
        } else if (piece == 3) {
            knightMoves(position);
        } else if (piece == 4) {
            cardinalMoves(position, true);
        } else if (piece == 5) {
            cardinalMoves(position, true);
            diagonalMoves(position, true);
        } else if (piece == 6) {
            cardinalMoves(position, false);
            diagonalMoves(position, false);
        }
    }
    public void findAllPossibleMoves(int color) {
        moveList.clear();
        for (Integer position : pieces.keySet()) {
            if (pieceColor(position) == color) {
                findPossibleMoves(position);
            }
        }
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
        moveList.clear();
        findPossibleMoves(position);
        if (Math.abs(pieces.get(position)) == 1) {
            pawnForwardMoves(position);
        }
        HashSet<Integer> legalMoves = new HashSet<>();
        HashSet<Integer> pieceMoves = new HashSet<>(moveList); // DOES THIS WORK???
        moveList.clear();
        for (Integer newPosition : pieceMoves) {
            if (!friendlySquare(newPosition, pieceColor(position))) {
                int capturedPiece = movePiece(position, newPosition);
                findAllPossibleMoves(-1 * pieceColor(position));
                if (!moveList.contains(findKingPos(pieceColor(position)))) {
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
            evaluation += pieceValues.get(pieces.get(position)) * pieceColor(position);
        }
        return evaluation;
    }
}
