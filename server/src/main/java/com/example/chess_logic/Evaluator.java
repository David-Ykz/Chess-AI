package com.example.chess_logic;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;

import java.util.HashMap;
import java.util.List;

public class Evaluator {
    public final HashMap<Piece, Integer> midgamePieceValues = new HashMap<>();
    public final HashMap<Piece, Integer> endgamePieceValues = new HashMap<>();
    public final HashMap<Square, Integer> squares = new HashMap<>();
    public final HashMap<Piece, Integer> whitePieces = new HashMap<>();
    public final HashMap<Piece, Integer> blackPieces = new HashMap<>();
    public final int[] midgameValues = {124, 781, 825, 1276, 2538, -124, -781, -825, -1276, -2538};
    public final int[] endgameValues = {206, 854, 915, 1380, 2682, -206, -854, -915, -1380, -2682};
    public final Piece[] piecesNoKing = {
            Piece.WHITE_PAWN, Piece.WHITE_BISHOP, Piece.WHITE_KNIGHT, Piece.WHITE_ROOK, Piece.WHITE_QUEEN,
            Piece.BLACK_PAWN, Piece.BLACK_BISHOP, Piece.BLACK_KNIGHT, Piece.BLACK_ROOK, Piece.BLACK_QUEEN
    };
    public final Piece[] piecesWithKing = {
            Piece.WHITE_PAWN, Piece.WHITE_BISHOP, Piece.WHITE_KNIGHT, Piece.WHITE_ROOK, Piece.WHITE_QUEEN, Piece.WHITE_KING,
            Piece.BLACK_PAWN, Piece.BLACK_BISHOP, Piece.BLACK_KNIGHT, Piece.BLACK_ROOK, Piece.BLACK_QUEEN, Piece.BLACK_KING
    };

    public Evaluator() {
        for (int i = 0; i < piecesNoKing.length; i++) {
            midgamePieceValues.put(piecesNoKing[i], midgameValues[i]);
            endgamePieceValues.put(piecesNoKing[i], endgameValues[i]);
        }

        for (int i = 0; i < piecesWithKing.length; i++) {
            if (i < piecesWithKing.length/2) {
                whitePieces.put(piecesWithKing[i], i);
            } else {
                blackPieces.put(piecesWithKing[i], i - 6);
            }
        }

        Square[] allSquares = Square.values();
        // Exclude Square.NONE
        for (int i = 0; i < allSquares.length - 1; i++) {
            squares.put(allSquares[i], i);
        }
    }

    public int getMidgamePieceValue(Piece pieceType) {
        return midgamePieceValues.get(pieceType);
    }

    public int midgamePSTBonus(Square square, Piece pieceType) {
        if (pieceType.getPieceSide() == Side.WHITE) {
            return PST.whiteMidgame[whitePieces.get(pieceType)][squares.get(square)];
        } else {
            return -PST.blackMidgame[blackPieces.get(pieceType)][squares.get(square)];
        }
    }


    public int midgameEval(Board board) {
        int totalEval = 0;
        for (Piece pieceType : midgamePieceValues.keySet()) {
            List<Square> piecePositions = board.getPieceLocation(pieceType);
            // Piece value
            totalEval += piecePositions.size() * midgamePieceValues.get(pieceType);
            // PST
            for (Square square : piecePositions) {
                totalEval += midgamePSTBonus(square, pieceType);
            }
        }
        // Tempo
        totalEval += 28 * (board.getSideToMove() == Side.WHITE ? 1 : -1);
        return totalEval;
    }

    public int endgameEval(Board board) {
        int totalEval = 0;
        for (Piece piece : endgamePieceValues.keySet()) {
            totalEval += board.getPieceLocation(piece).size() * endgamePieceValues.get(piece);
        }
        return totalEval;
    }

    public int phase(Board board) {
        final int midgameLimit = 15258;
        final int endgameLimit = 3915;

        int npm = Math.max(endgameLimit, Math.min(nonPawnMaterial(board), midgameLimit));

        return (((npm - endgameLimit) * 128) / (midgameLimit - endgameLimit));
    }


    public int isolatedPawns(Board board) {
        long whiteBitboard = board.getBitboard(Piece.WHITE_PAWN);

        return 0;
    }

    public int nonPawnMaterial(Board board) {
        int totalEval = 0;
        for (Piece piece : midgamePieceValues.keySet()) {
            if (piece.equals(Piece.WHITE_PAWN) || piece.equals(Piece.BLACK_PAWN)) {
                totalEval += board.getPieceLocation(piece).size() * midgamePieceValues.get(piece);
            }
        }
        return totalEval;
    }


    public int evaluate(Board board) {
        // Tempo: side to move gets +28
        // Midgame piece values: [124, 781, 825, 1276, 2538]
        // Not midgame piece values: [206, 854, 915, 1380, 2682]

        int totalEval = 0;
        int mgEval = midgameEval(board);
//        int egEval = endgameEval(board);
        totalEval += mgEval;
        return totalEval;
    }


}
