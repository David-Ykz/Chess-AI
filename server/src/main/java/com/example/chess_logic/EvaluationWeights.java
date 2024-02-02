/**
 * Stores all piece square tables
 * Uses array of boards to encourage pieces to move to good positions
 * @author David Ye
 */
package com.example.chess_logic;

import com.github.bhlangonijr.chesslib.*;

import java.util.HashMap;

public class EvaluationWeights {

    public final HashMap<Piece, Integer> midgamePieceValues = new HashMap<>();
    public final HashMap<Piece, Integer> endgamePieceValues = new HashMap<>();
    public final HashMap<Piece, Integer> pieceIntegerMap = new HashMap<>();
    public final HashMap<String, Integer> listOfLetters = new HashMap<>();
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


    //<editor-fold desc="whiteMidgamePST">
    public final int[][] whiteMidgamePST = {
            // Pawn
            {
                    0, 0, 0, 0, 0, 0, 0, 0,
                    3, 3, 10, 19, 16, 19, 7, -5,
                    -9, -15, 11, 15, 32, 22, 5, -22,
                    -4, -23, 6, 20, 40, 17, 4, -8,
                    13, 0, -13, 1, 11, -2, -13, 5,
                    5, -12, -7, 22, -8, -5, -15, -8,
                    -7, 7, -3, -13, 5, -16, 10, -8,
                    0, 0, 0, 0, 0, 0, 0, 0
            },
            // Knight
            {
                    -175, -92, -74, -73, -73, -74, -92, -175,
                    -77, -41, -27, -15, -15, -27, -41, -77,
                    -61, -17, 6, 12, 12, 6, -17, -61,
                    -35, 8, 40, 49, 49, 40, 8, -35,
                    -34, 13, 44, 51, 51, 44, 13, -34,
                    -9, 22, 58, 53, 53, 58, 22, -9,
                    -67, -27, 4, 37, 37, 4, -27, -67,
                    -201, -83, -56, -26, -26, -56, -83, -201
            },
            // Bishop
            {
                    -53, -5, -8, -23, -23, -8, -5, -53,
                    -15, 8, 19, 4, 4, 19, 8, -15,
                    -7, 21, -5, 17, 17, -5, 21, -7,
                    -5, 11, 25, 39, 39, 25, 11, -5,
                    -12, 29, 22, 31, 31, 22, 29, -12,
                    -16, 6, 1, 11, 11, 1, 6, -16,
                    -17, -14, 5, 0, 0, 5, -14, -17,
                    -48, 1, -14, -23, -23, -14, 1, -48
            },
            // Rook
            {
                    -31, -20, -14, -5, -5, -14, -20, -31,
                    -21, -13, -8, 6, 6, -8, -13, -21,
                    -25, -11, -1, 3, 3, -1, -11, -25,
                    -13, -5, -4, -6, -6, -4, -5, -13,
                    -27, -15, -4, 3, 3, -4, -15, -27,
                    -22, -2, 6, 12, 12, 6, -2, -22,
                    -2, 12, 16, 18, 18, 16, 12, -2,
                    -17, -19, -1, 9, 9, -1, -19, -17
            },
            // Queen
            {
                    3, -5, -5, 4, 4, -5, -5, 3,
                    -3, 5, 8, 12, 12, 8, 5, -3,
                    -3, 6, 13, 7, 7, 13, 6, -3,
                    4, 5, 9, 8, 8, 9, 5, 4,
                    0, 14, 12, 5, 5, 12, 14, 0,
                    -4, 10, 6, 8, 8, 6, 10, -4,
                    -5, 6, 10, 8, 8, 10, 6, -5,
                    -2, -2, 1, -2, -2, 1, -2, -2
            },
            // King
            {
                    271, 327, 271, 198, 198, 271, 327, 271,
                    278, 303, 234, 179, 179, 234, 303, 278,
                    195, 258, 169, 120, 120, 169, 258, 195,
                    164, 190, 138, 98, 98, 138, 190, 164,
                    154, 179, 105, 70, 70, 105, 179, 154,
                    123, 145, 81, 31, 31, 81, 145, 123,
                    88, 120, 65, 33, 33, 65, 120, 88,
                    59, 89, 45, -1, -1, 45, 89, 59
            }
    };
    //</editor-fold>

    //<editor-fold desc="blackMidgamePST">
    public final int[][] blackMidgamePST = {
            // Pawn
            {
                    0, 0, 0, 0, 0, 0, 0, 0,
                    -7, 7, -3, -13, 5, -16, 10, -8,
                    5, -12, -7, 22, -8, -5, -15, -8,
                    13, 0, -13, 1, 11, -2, -13, 5,
                    -4, -23, 6, 20, 40, 17, 4, -8,
                    -9, -15, 11, 15, 32, 22, 5, -22,
                    3, 3, 10, 19, 16, 19, 7, -5,
                    0, 0, 0, 0, 0, 0, 0, 0
            },
            // Knight
            {
                    -201, -83, -56, -26, -26, -56, -83, -201,
                    -67, -27, 4, 37, 37, 4, -27, -67,
                    -9, 22, 58, 53, 53, 58, 22, -9,
                    -34, 13, 44, 51, 51, 44, 13, -34,
                    -35, 8, 40, 49, 49, 40, 8, -35,
                    -61, -17, 6, 12, 12, 6, -17, -61,
                    -77, -41, -27, -15, -15, -27, -41, -77,
                    -175, -92, -74, -73, -73, -74, -92, -175
            },
            // Bishop
            {
                    -48, 1, -14, -23, -23, -14, 1, -48,
                    -17, -14, 5, 0, 0, 5, -14, -17,
                    -16, 6, 1, 11, 11, 1, 6, -16,
                    -12, 29, 22, 31, 31, 22, 29, -12,
                    -5, 11, 25, 39, 39, 25, 11, -5,
                    -7, 21, -5, 17, 17, -5, 21, -7,
                    -15, 8, 19, 4, 4, 19, 8, -15,
                    -53, -5, -8, -23, -23, -8, -5, -53
            },
            // Rook
            {
                    -17, -19, -1, 9, 9, -1, -19, -17,
                    -2, 12, 16, 18, 18, 16, 12, -2,
                    -22, -2, 6, 12, 12, 6, -2, -22,
                    -27, -15, -4, 3, 3, -4, -15, -27,
                    -13, -5, -4, -6, -6, -4, -5, -13,
                    -25, -11, -1, 3, 3, -1, -11, -25,
                    -21, -13, -8, 6, 6, -8, -13, -21,
                    -31, -20, -14, -5, -5, -14, -20, -31
            },
            // Queen
            {
                    -2, -2, 1, -2, -2, 1, -2, -2,
                    -5, 6, 10, 8, 8, 10, 6, -5,
                    -4, 10, 6, 8, 8, 6, 10, -4,
                    0, 14, 12, 5, 5, 12, 14, 0,
                    4, 5, 9, 8, 8, 9, 5, 4,
                    -3, 6, 13, 7, 7, 13, 6, -3,
                    -3, 5, 8, 12, 12, 8, 5, -3,
                    3, -5, -5, 4, 4, -5, -5, 3
            },
            // King
            {
                    59, 89, 45, -1, -1, 45, 89, 59,
                    88, 120, 65, 33, 33, 65, 120, 88,
                    123, 145, 81, 31, 31, 81, 145, 123,
                    154, 179, 105, 70, 70, 105, 179, 154,
                    164, 190, 138, 98, 98, 138, 190, 164,
                    195, 258, 169, 120, 120, 169, 258, 195,
                    278, 303, 234, 179, 179, 234, 303, 278,
                    271, 327, 271, 198, 198, 271, 327, 271
            }
    };
    //</editor-fold>

    //<editor-fold desc="whiteEndgamePST">
    public final int[][] whiteEndgamePST = {
            // Pawn
            {
                    0, 0, 0, 0, 0, 0, 0, 0,
                    -10, -6, 10, 0, 14, 7, -5, -19,
                    -10, -10, -10, 4, 4, 3, -6, -4,
                    6, -2, -8, -4, -13, -12, -10, -9,
                    10, 5, 4, -5, -5, -5, 14, 9,
                    28, 20, 21, 28, 30, 7, 6, 13,
                    0, -11, 12, 21, 25, 19, 4, 7,
                    0, 0, 0, 0, 0, 0, 0, 0
            },
            // Knight
            {
                    -96, -65, -49, -21, -21, -49, -65, -96,
                    -67, -54, -18, 8, 8, -18, -54, -67,
                    -40, -27, -8, 29, 29, -8, -27, -40,
                    -35, -2, 13, 28, 28, 13, -2, -35,
                    -45, -16, 9, 39, 39, 9, -16, -45,
                    -51, -44, -16, 17, 17, -16, -44, -51,
                    -69, -50, -51, 12, 12, -51, -50, -69,
                    -100, -88, -56, -17, -17, -56, -88, -100
            },
            // Bishop
            {
                    -57, -30, -37, -12, -12, -37, -30, -57,
                    -37, -13, -17, 1, 1, -17, -13, -37,
                    -16, -1, -2, 10, 10, -2, -1, -16,
                    -20, -6, 0, 17, 17, 0, -6, -20,
                    -17, -1, -14, 15, 15, -14, -1, -17,
                    -30, 6, 4, 6, 6, 4, 6, -30,
                    -31, -20, -1, 1, 1, -1, -20, -31,
                    -46, -42, -37, -24, -24, -37, -42, -46
            },
            // Rook
            {
                    -9, -13, -10, -9, -9, -10, -13, -9,
                    -12, -9, -1, -2, -2, -1, -9, -12,
                    6, -8, -2, -6, -6, -2, -8, 6,
                    -6, 1, -9, 7, 7, -9, 1, -6,
                    -5, 8, 7, -6, -6, 7, 8, -5,
                    6, 1, -7, 10, 10, -7, 1, 6,
                    4, 5, 20, -5, -5, 20, 5, 4,
                    18, 0, 19, 13, 13, 19, 0, 18
            },
            // Queen
            {
                    -69, -57, -47, -26, -26, -47, -57, -69,
                    -55, -31, -22, -4, -4, -22, -31, -55,
                    -39, -18, -9, 3, 3, -9, -18, -39,
                    -23, -3, 13, 24, 24, 13, -3, -23,
                    -29, -6, 9, 21, 21, 9, -6, -29,
                    -38, -18, -12, 1, 1, -12, -18, -38,
                    -50, -27, -24, -8, -8, -24, -27, -50,
                    -75, -52, -43, -36, -36, -43, -52, -75
            },
            // King
            {
                    1, 45, 85, 76, 76, 85, 45, 1,
                    53, 100, 133, 135, 135, 133, 100, 53,
                    88, 130, 169, 175, 175, 169, 130, 88,
                    103, 156, 172, 172, 172, 172, 156, 103,
                    96, 166, 199, 199, 199, 199, 166, 96,
                    92, 172, 184, 191, 191, 184, 172, 92,
                    47, 121, 116, 131, 131, 116, 121, 47,
                    11, 59, 73, 78, 78, 73, 59, 11
            }
    };
    //</editor-fold>

    //<editor-fold desc="blackEndgamePST">
    public final int[][] blackEndgamePST = {
            // Pawn
            {
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, -11, 12, 21, 25, 19, 4, 7,
                    28, 20, 21, 28, 30, 7, 6, 13,
                    10, 5, 4, -5, -5, -5, 14, 9,
                    6, -2, -8, -4, -13, -12, -10, -9,
                    -10, -10, -10, 4, 4, 3, -6, -4,
                    -10, -6, 10, 0, 14, 7, -5, -19,
                    0, 0, 0, 0, 0, 0, 0, 0
            },
            // Knight
            {
                    -100, -88, -56, -17, -17, -56, -88, -100,
                    -69, -50, -51, 12, 12, -51, -50, -69,
                    -51, -44, -16, 17, 17, -16, -44, -51,
                    -45, -16, 9, 39, 39, 9, -16, -45,
                    -35, -2, 13, 28, 28, 13, -2, -35,
                    -40, -27, -8, 29, 29, -8, -27, -40,
                    -67, -54, -18, 8, 8, -18, -54, -67,
                    -96, -65, -49, -21, -21, -49, -65, -96
            },
            // Bishop
            {
                    -46, -42, -37, -24, -24, -37, -42, -46,
                    -31, -20, -1, 1, 1, -1, -20, -31,
                    -30, 6, 4, 6, 6, 4, 6, -30,
                    -17, -1, -14, 15, 15, -14, -1, -17,
                    -20, -6, 0, 17, 17, 0, -6, -20,
                    -16, -1, -2, 10, 10, -2, -1, -16,
                    -37, -13, -17, 1, 1, -17, -13, -37,
                    -57, -30, -37, -12, -12, -37, -30, -57
            },
            // Rook
            {
                    18, 0, 19, 13, 13, 19, 0, 18,
                    4, 5, 20, -5, -5, 20, 5, 4,
                    6, 1, -7, 10, 10, -7, 1, 6,
                    -5, 8, 7, -6, -6, 7, 8, -5,
                    -6, 1, -9, 7, 7, -9, 1, -6,
                    6, -8, -2, -6, -6, -2, -8, 6,
                    -12, -9, -1, -2, -2, -1, -9, -12,
                    -9, -13, -10, -9, -9, -10, -13, -9
            },
            // Queen
            {
                    -75, -52, -43, -36, -36, -43, -52, -75,
                    -50, -27, -24, -8, -8, -24, -27, -50,
                    -38, -18, -12, 1, 1, -12, -18, -38,
                    -29, -6, 9, 21, 21, 9, -6, -29,
                    -23, -3, 13, 24, 24, 13, -3, -23,
                    -39, -18, -9, 3, 3, -9, -18, -39,
                    -55, -31, -22, -4, -4, -22, -31, -55,
                    -69, -57, -47, -26, -26, -47, -57, -69
            },
            // King
            {
                    11, 59, 73, 78, 78, 73, 59, 11,
                    47, 121, 116, 131, 131, 116, 121, 47,
                    92, 172, 184, 191, 191, 184, 172, 92,
                    96, 166, 199, 199, 199, 199, 166, 96,
                    103, 156, 172, 172, 172, 172, 156, 103,
                    88, 130, 169, 175, 175, 169, 130, 88,
                    53, 100, 133, 135, 135, 133, 100, 53,
                    1, 45, 85, 76, 76, 85, 45, 1
            }
    };
    //</editor-fold>

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

    EvaluationWeights() {
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

    public int midgamePSTBonus(Square square, Piece pieceType) {
        if (pieceType.getPieceSide() == Side.WHITE) {
            return whiteMidgamePST[whitePieces.get(pieceType)][squares.get(square)];
        } else {
            return -blackMidgamePST[blackPieces.get(pieceType)][squares.get(square)];
        }
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
