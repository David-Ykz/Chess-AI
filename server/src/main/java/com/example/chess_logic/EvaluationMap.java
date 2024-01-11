/**
 * Stores all piece square tables
 * Uses array of boards to encourage pieces to move to good positions
 * @author David Ye
 */
package com.example.chess_logic;

import com.github.bhlangonijr.chesslib.*;

import java.util.HashMap;

public class EvaluationMap {
    public final HashMap<Piece, Integer> pieceValueMap = new HashMap<>();
    public final HashMap<Piece, Integer> pieceIntegerMap = new HashMap<>();
    public final HashMap<String, Integer> listOfLetters = new HashMap<>();



    // P B N R K
    public final int[][] openingTable = {
            {
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 1, 1, 0, 0, 0,
                    1, 1, 4, 5, 5, 0, 0, 0,
                    1, 1, 3, 3, 3, 0, 0, 1,
                    0, 0, -1, -2, -2, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0
            },

            {
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 5, 0, 1, 1, 0, 5, 0,
                    0, 0, 5, 1, 1, 5, 0, 0,
                    0, 1, 0, 0, 0, 0, 1, 0,
                    0, 3, 0, 2, 2, 0, 3, 0,
                    0, 0, -1, 0, 0, 0, -1, 0
            },

            {
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 1, 0, 1, 1, 0, 1, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 3, 0, 0, 3, 0, 0,
                    0, 0, 0, 2, 2, 0, 0, 0,
                    0, -1, 0, 0, 0, 0, -1, 0
            },

            {
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    1, -1, 0, 1, 1, 1, -1, 1
            },

            {
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 1, 1, 0, 0, 0, 0,
                    0, 0, 0, 6, 0, 0, 0, 0
            },

            {
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 2, 0, 2, 0, 2, 0
            }
    };

    // Distance from the center
    public final int[] centerManhattanDistance = {
            6, 5, 4, 3, 3, 4, 5, 6,
            5, 4, 3, 2, 2, 3, 4, 5,
            4, 3, 2, 1, 1, 2, 3, 4,
            3, 2, 1, 0, 0, 1, 2, 3,
            3, 2, 1, 0, 0, 1, 2, 3,
            4, 3, 2, 1, 1, 2, 3, 4,
            5, 4, 3, 2, 2, 3, 4, 5,
            6, 5, 4, 3, 3, 4, 5, 6
    };

    EvaluationMap() {
        pieceValueMap.put(Piece.WHITE_PAWN, 100);
        pieceValueMap.put(Piece.WHITE_BISHOP, 300);
        pieceValueMap.put(Piece.WHITE_KNIGHT, 300);
        pieceValueMap.put(Piece.WHITE_ROOK, 500);
        pieceValueMap.put(Piece.WHITE_QUEEN, 900);
        pieceValueMap.put(Piece.WHITE_KING, 4000);
        pieceValueMap.put(Piece.BLACK_PAWN, -100);
        pieceValueMap.put(Piece.BLACK_BISHOP, -300);
        pieceValueMap.put(Piece.BLACK_KNIGHT, -300);
        pieceValueMap.put(Piece.BLACK_ROOK, -500);
        pieceValueMap.put(Piece.BLACK_QUEEN, -900);
        pieceValueMap.put(Piece.BLACK_KING, -4000);

        pieceIntegerMap.put(Piece.WHITE_PAWN, 1);
        pieceIntegerMap.put(Piece.WHITE_BISHOP, 2);
        pieceIntegerMap.put(Piece.WHITE_KNIGHT, 3);
        pieceIntegerMap.put(Piece.WHITE_ROOK, 4);
        pieceIntegerMap.put(Piece.WHITE_QUEEN, 5);
        pieceIntegerMap.put(Piece.WHITE_KING, 6);
        pieceIntegerMap.put(Piece.BLACK_PAWN, -1);
        pieceIntegerMap.put(Piece.BLACK_BISHOP, -2);
        pieceIntegerMap.put(Piece.BLACK_KNIGHT, -3);
        pieceIntegerMap.put(Piece.BLACK_ROOK, -4);
        pieceIntegerMap.put(Piece.BLACK_QUEEN, -5);
        pieceIntegerMap.put(Piece.BLACK_KING, -6);



        listOfLetters.put("A", 0);
        listOfLetters.put("B", 1);
        listOfLetters.put("C", 2);
        listOfLetters.put("D", 3);
        listOfLetters.put("E", 4);
        listOfLetters.put("F", 5);
        listOfLetters.put("G", 6);
        listOfLetters.put("H", 7);

    }


    public int squareToArrIndex(Square square, int color) {
        String value = square.value();
        if (color > 0) {
            return listOfLetters.get(value.substring(0, 1)) + (8 - Integer.parseInt(value.substring(1))) * 8;
        } else {
            return listOfLetters.get(value.substring(0, 1)) + (Integer.parseInt(value.substring(1)) - 1) * 8;
        }
    }

    public int openingEvaluation(Board board, Piece piece) {
        int totalEval = 0;
        int pieceValue = pieceIntegerMap.get(piece);
        for (Square square : board.getPieceLocation(piece)) {
            totalEval += openingTable[Math.abs(pieceValue) - 1][squareToArrIndex(square, pieceValue)];
        }
        return totalEval;
    }

    // Finds how far a piece is from the center of the board
    public int findCMD(Square square, int color) {
        return centerManhattanDistance[squareToArrIndex(square, color)];
    }

    // Returns the distance between the 2 kings
    public int findMD(Board board) {
        Square whiteKing = board.getKingSquare(Side.WHITE);
        Square blackKing = board.getKingSquare(Side.BLACK);
        int xWhitePos = listOfLetters.get(whiteKing.value().substring(0, 1));
        int yWhitePos = Integer.parseInt(whiteKing.value().substring(1));
        int xBlackPos = listOfLetters.get(blackKing.value().substring(0, 1));
        int yBlackPos = Integer.parseInt(blackKing.value().substring(1));
        return Math.abs(xWhitePos - xBlackPos) + Math.abs(yWhitePos - yBlackPos);
    }
}
