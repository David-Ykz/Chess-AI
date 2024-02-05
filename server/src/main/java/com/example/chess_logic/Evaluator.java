package com.example.chess_logic;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Evaluator {
    public final int MIDGAME_BISHOP_PAIR_BONUS = 110;
    public final int ENDGAME_BISHOP_PAIR_BONUS = 190;
    public final int PAWN_ATTACK_SQUARE_PENALTY = 30;
    public final int GAME_PHASE_THRESHOLD = 1600;
    public final int CHECKMATE_EVALUATION = 999999;
    public final int CHECKMATE_DEPTH_INCENTIVE = 10000;

    public final HashMap<Piece, Integer> midgamePieceValues = new HashMap<>();
    public final HashMap<Piece, Integer> endgamePieceValues = new HashMap<>();
    public final HashMap<Square, Integer> squares = new HashMap<>();
    public final HashMap<Piece, Integer> whitePieces = new HashMap<>();
    public final HashMap<Piece, Integer> blackPieces = new HashMap<>();
    public final int[] midgameValues = {100, 300, 300, 500, 900, -100, -300, -300, -500, -900};
    public final int[] endgameValues = {150, 300, 320, 510, 900, -150, -300, -320, -510, -900};
//    public final int[] midgameValues = {124, 781, 825, 1276, 2538, -124, -781, -825, -1276, -2538};
//    public final int[] endgameValues = {206, 854, 915, 1380, 2682, -206, -854, -915, -1380, -2682};
    public final Piece[] piecesNoKing = {
            Piece.WHITE_PAWN, Piece.WHITE_KNIGHT, Piece.WHITE_BISHOP, Piece.WHITE_ROOK, Piece.WHITE_QUEEN,
            Piece.BLACK_PAWN, Piece.BLACK_KNIGHT, Piece.BLACK_BISHOP, Piece.BLACK_ROOK, Piece.BLACK_QUEEN
    };
    public final Piece[] piecesWithKing = {
            Piece.WHITE_PAWN, Piece.WHITE_KNIGHT, Piece.WHITE_BISHOP, Piece.WHITE_ROOK, Piece.WHITE_QUEEN, Piece.WHITE_KING,
            Piece.BLACK_PAWN, Piece.BLACK_KNIGHT, Piece.BLACK_BISHOP, Piece.BLACK_ROOK, Piece.BLACK_QUEEN, Piece.BLACK_KING
    };

    public final int[][] midgameMobilityValues = { // NBRQK
            {-62,-53,-12,-4,3,13,22,28,33},
            {-48,-20,16,26,38,51,55,63,63,68,81,81,91,98},
            {-60,-20,2,3,3,11,22,31,40,40,41,48,57,57,62},
            {-30,-12,-8,-9,20,23,23,35,38,53,64,65,65,66,67,67,72,72,77,79,93,108,108,108,110,114,114,116},
            {-10,0,-5,-10,-30,-60,-80,-90,-90}
    };

    public final int[][] endgameMobilityValues = { // NBRQK
            {-81,-56,-31,-16,5,11,17,20,25},
            {-59,-23,-3,13,24,42,54,57,65,73,78,86,88,97},
            {-78,-17,23,39,70,99,103,121,134,139,158,164,168,169,172},
            {-48,-30,-7,19,40,55,59,75,78,96,96,100,121,127,131,133,136,141,147,150,151,168,168,171,182,182,192,219},
            {-80,-60,-50,-40,5,20,30,40,40}
    };
    public final int[][] blockingPawnPenalty = { // NBRQK
            {0, 0, 0, -20, -20, 0, 0, 0},
            {0, 0, -40, -50, -50, -20, 0, 0},
            {0, 0, 0, -60, -60, 0, 0, 0},
            {-5, -5, -40, -50, -50, -20, -5, -5},
            {-100, -100, -100, -100, -100, -100, -100, -100}
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

    public int midgameMobilityBonus(Board board, Piece pieceType, int numPieces) {
        Side currentSide = board.getSideToMove();
        board.setSideToMove(pieceType.getPieceSide());
        List<Move> moves = new ArrayList<>();
        if (pieceType == Piece.WHITE_KNIGHT || pieceType == Piece.BLACK_KNIGHT) {
            board.setSideToMove(pieceType.getPieceSide());
            MoveGenerator.generateKnightMoves(board, moves);
        } else if (pieceType == Piece.WHITE_BISHOP || pieceType == Piece.BLACK_BISHOP) {
            MoveGenerator.generateBishopMoves(board, moves);
        } else if (pieceType == Piece.WHITE_ROOK || pieceType == Piece.BLACK_ROOK) {
            MoveGenerator.generateRookMoves(board, moves);
        } else if (pieceType == Piece.WHITE_QUEEN || pieceType == Piece.BLACK_QUEEN) {
            MoveGenerator.generateQueenMoves(board, moves);
        } else if (pieceType == Piece.WHITE_KING || pieceType == Piece.BLACK_KING) {
            MoveGenerator.generateKingMoves(board, moves);
        }

        board.setSideToMove(currentSide);
        if (pieceType.getPieceSide() == Side.WHITE) {
            int pieceIndex = whitePieces.get(pieceType) - 1;
            return midgameMobilityValues[pieceIndex][Math.min(moves.size()/numPieces, midgameMobilityValues[pieceIndex].length - 1)];
        } else {
            int pieceIndex = blackPieces.get(pieceType) - 1;
            return -midgameMobilityValues[pieceIndex][Math.min(moves.size()/numPieces, midgameMobilityValues[pieceIndex].length - 1)];
        }
    }

    public int endgamePSTBonus(Square square, Piece pieceType) {
        if (pieceType.getPieceSide() == Side.WHITE) {
            return PST.whiteEndgame[whitePieces.get(pieceType)][squares.get(square)];
        } else {
            return -PST.blackEndgame[blackPieces.get(pieceType)][squares.get(square)];
        }
    }

    public int endgameMobilityBonus(Board board, Piece pieceType, int numPieces) {
        Side currentSide = board.getSideToMove();
        board.setSideToMove(pieceType.getPieceSide());
        List<Move> moves = new ArrayList<>();
        if (pieceType == Piece.WHITE_KNIGHT || pieceType == Piece.BLACK_KNIGHT) {
            board.setSideToMove(pieceType.getPieceSide());
            MoveGenerator.generateKnightMoves(board, moves);
        } else if (pieceType == Piece.WHITE_BISHOP || pieceType == Piece.BLACK_BISHOP) {
            MoveGenerator.generateBishopMoves(board, moves);
        } else if (pieceType == Piece.WHITE_ROOK || pieceType == Piece.BLACK_ROOK) {
            MoveGenerator.generateRookMoves(board, moves);
        } else if (pieceType == Piece.WHITE_QUEEN || pieceType == Piece.BLACK_QUEEN) {
            MoveGenerator.generateQueenMoves(board, moves);
        } else if (pieceType == Piece.WHITE_KING || pieceType == Piece.BLACK_KING) {
            MoveGenerator.generateKingMoves(board, moves);
        }

        board.setSideToMove(currentSide);
        if (pieceType.getPieceSide() == Side.WHITE) {
            int pieceIndex = whitePieces.get(pieceType) - 1;
            return endgameMobilityValues[pieceIndex][Math.min(moves.size()/numPieces, endgameMobilityValues[pieceIndex].length - 1)];
        } else {
            int pieceIndex = blackPieces.get(pieceType) - 1;
            return -endgameMobilityValues[pieceIndex][Math.min(moves.size()/numPieces, endgameMobilityValues[pieceIndex].length - 1)];
        }
    }

    public int blockingPawn(Board board, Square square, Piece pieceType) {
        int squareIndex = squares.get(square);
        if (pieceType.getPieceSide() == Side.WHITE && squareIndex > 15) {
            Square squareBelow = Square.values()[squareIndex - 8];
            if (board.getPiece(squareBelow) == Piece.WHITE_PAWN) {
                return blockingPawnPenalty[whitePieces.get(pieceType) - 1][squareIndex % 8];
            }
        } else if (pieceType.getPieceSide() == Side.BLACK && squareIndex < 48) {
            Square squareAbove = Square.values()[squareIndex + 8];
            if (board.getPiece(squareAbove) == Piece.BLACK_PAWN) {
                return -blockingPawnPenalty[blackPieces.get(pieceType) - 1][squareIndex % 8];
            }
        }
        return 0;
    }

    public int midgameEval(Board board) {
        int totalEval = 0;
        for (Piece pieceType : piecesWithKing) {
            List<Square> piecePositions = board.getPieceLocation(pieceType);
            if (!piecePositions.isEmpty()) {
                // Piece value
                if (pieceType != Piece.WHITE_KING && pieceType != Piece.BLACK_KING) {
                    totalEval += piecePositions.size() * midgamePieceValues.get(pieceType);
                }
                // Bishop pair
                if (pieceType == Piece.WHITE_BISHOP && piecePositions.size() == 2) {
                    totalEval += MIDGAME_BISHOP_PAIR_BONUS;
                } else if (pieceType == Piece.BLACK_BISHOP && piecePositions.size() == 2) {
                    totalEval -= MIDGAME_BISHOP_PAIR_BONUS;
                }
                // PST
                for (Square square : piecePositions) {
                    totalEval += midgamePSTBonus(square, pieceType);
                    // Blocking pawn penalty
                    if (pieceType != Piece.WHITE_PAWN && pieceType != Piece.BLACK_PAWN) {
                        totalEval += blockingPawn(board, square, pieceType);
                    }
                }
                // Mobility
                if (pieceType != Piece.WHITE_PAWN && pieceType != Piece.BLACK_PAWN) {
                    totalEval += midgameMobilityBonus(board, pieceType, piecePositions.size());
                }
            }
        }
        return totalEval;
    }

    public int endgameEval(Board board) {
        int totalEval = 0;
        for (Piece pieceType : piecesWithKing) {
            List<Square> piecePositions = board.getPieceLocation(pieceType);
            if (!piecePositions.isEmpty()) {
                // Piece value
                if (pieceType != Piece.WHITE_KING && pieceType != Piece.BLACK_KING) {
                    totalEval += piecePositions.size() * endgamePieceValues.get(pieceType);
                }
                // Bishop pair
                if (pieceType == Piece.WHITE_BISHOP && piecePositions.size() == 2) {
                    totalEval += ENDGAME_BISHOP_PAIR_BONUS;
                } else if (pieceType == Piece.BLACK_BISHOP && piecePositions.size() == 2) {
                    totalEval -= ENDGAME_BISHOP_PAIR_BONUS;
                }
                // PST
                for (Square square : piecePositions) {
                    totalEval += endgamePSTBonus(square, pieceType);
                    // Blocking pawn penalty
                    if (pieceType != Piece.WHITE_PAWN && pieceType != Piece.BLACK_PAWN) {
                        totalEval += blockingPawn(board, square, pieceType);
                    }
                }
                // Mobility
                if (pieceType != Piece.WHITE_PAWN && pieceType != Piece.BLACK_PAWN) {
                    totalEval += endgameMobilityBonus(board, pieceType, piecePositions.size());
                }
            }
        }
        return totalEval;
    }

    public int phase(Board board) {
        final int midgameLimit = 15258;
        final int endgameLimit = 2100;

        int npm = Math.max(endgameLimit, Math.min(nonPawnMaterial(board), midgameLimit));

        return (((npm - endgameLimit) * 128) / (midgameLimit - endgameLimit));
    }

    public int numPiecesLeft(Board board) {
        int totalPieces = 0;
        for (Piece pieceType : piecesWithKing) {
            totalPieces += board.getPieceLocation(pieceType).size();
        }
        return totalPieces;
    }

    public int doubledPawns(Board board) {
        long whiteBitboard = board.getBitboard(Piece.WHITE_PAWN);

        return 0;
    }

//    public int kingSafety(Board board) {
//        board.getKingSquare(Side.WHITE)
//    }

    public int nonPawnMaterial(Board board) {
        int totalEval = 0;
        for (Piece piece : midgamePieceValues.keySet()) {
            if (!piece.equals(Piece.WHITE_PAWN) && !piece.equals(Piece.BLACK_PAWN)) {
                totalEval += Math.abs(board.getPieceLocation(piece).size() * midgamePieceValues.get(piece));
            }
        }
        return totalEval;
    }

    public int numPawnsLeft(Board board) {
        int whitePawns = board.getPieceLocation(Piece.WHITE_PAWN).size();
        int blackPawns = board.getPieceLocation(Piece.BLACK_PAWN).size();
        return Math.max(whitePawns, blackPawns);
    }


    public int evaluate(Board board) {
        int mgEval = midgameEval(board);
        int egEval = endgameEval(board);
        int notPawns = nonPawnMaterial(board);
        int numPawns = numPawnsLeft(board);

        if (notPawns <= GAME_PHASE_THRESHOLD) {
            return egEval;
        }

        return mgEval;
    }


}
