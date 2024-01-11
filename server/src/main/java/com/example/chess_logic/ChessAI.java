/**
 * Custom chess engine using minimax search
 * Optimizations used: alpha-beta pruning & move ordering, quiescence search
 * @author David Ye
 */

package com.example.chess_logic;
import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.Move;
import org.springframework.core.metrics.StartupStep;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.*;

public class ChessAI {
    // Performance Statistics //
    public int nodesSearched = 0;
    public int numPruned = 0;
    public EvaluationMap evalMap = new EvaluationMap();

    public ChessAI() {
    }

    public void printPerformanceInfo() {
        System.out.println("Nodes searched: " + nodesSearched);
        System.out.println("Nodes Pruned: " + numPruned);
    }

    public int evaluate(Board board) {
        int totalEval = basicEval(board);
        int numPieces = 0;
        for (Piece piece : evalMap.pieceValueMap.keySet()) {
            numPieces += board.getPieceLocation(piece).size();
        }
        if (numPieces > 5) {
            totalEval += openingEval(board);
        } else {
            totalEval += endgameEval(board);
        }
        return totalEval;
    }

    public int basicEval(Board board) {
        int totalEval = 0;
        for (Piece piece : evalMap.pieceValueMap.keySet()) {
            totalEval += board.getPieceLocation(piece).size() * evalMap.pieceValueMap.get(piece);
        }
        return totalEval;
    }

    public int openingEval(Board board) {
        int totalEval = 0;
        for (Piece piece : evalMap.pieceIntegerMap.keySet()) {
            totalEval += evalMap.openingEvaluation(board, piece) * (piece.getPieceSide() == Side.WHITE ? 1 : -1);
        }
        return totalEval;
    }

    public int endgameEval(Board board) {
        int totalEval = 0;
        if (basicEval(board) > 200) {
            totalEval += 470 * evalMap.findCMD(board.getKingSquare(Side.BLACK), -1);
            totalEval += 160 * (14 - evalMap.findMD(board));
        } else if (basicEval(board) < -200) {
            totalEval -= 470 * evalMap.findCMD(board.getKingSquare(Side.WHITE), 1);
            totalEval -= 160 * (14 - evalMap.findMD(board));
        }
        return totalEval;
    }

    public EvalMove maxMove(EvalMove a, EvalMove b) {
        return a.eval >= b.eval ? a : b;
    }
    public EvalMove minMove(EvalMove a, EvalMove b) {
        return a.eval <= b.eval ? a : b;
    }

    // Sorts the moves so better moves are searched first
//    public ArrayList<Move> orderMoves(Board board, List<Move> moves) {
//        ArrayList<Move> orderedMoves = new ArrayList<>(moves.size());
//        for (Move move : moves) {
//            board.getPiece(.getFrom()
//            int moveScore = 0;
//            int piece = board.getPieces().get(move.getOldPosition()) % 10;
//            if (board.getPieces().containsKey(move.getNewPosition())) {
//                int capturedPiece = board.getPieces().get(move.getNewPosition());
//                // Capturing a piece with a piece of less value is a bonus
//                moveScore += 10 * (int)board.pieceValues[Math.abs(capturedPiece % 10) - 1] - (int)board.pieceValues[Math.abs(piece % 10) - 1];
//                // An exception is made for the king, as any legal capture with the king is good
//                if (Math.abs(piece) % 10 == 6) {
//                    moveScore += (int)board.pieceValues[Math.abs(piece % 10) - 1];
//                }
//            }
//            // Discourages moving to a square attacked by an enemy pawn
//            if (board.allPawnSquares(-color).contains(move.getNewPosition())) {
//                moveScore -= 1;
//            }
//            // Encourages promoting pawns
//            moveScore += Math.abs(move.getPromotedPiece() % 10);
//            move.setMoveScore(moveScore);
//            orderedMoves.add(move);
//        }
//        // Sorts the moves so moves with higher scores are at the beginning of the arraylist
//        Collections.sort(orderedMoves);
//        return orderedMoves;
//    }

    public EvalMove quiescenceSearch(Board board, int alpha, int beta) {
        nodesSearched++;
        EvalMove bestMove = new EvalMove(evaluate(board));

        if (board.getSideToMove() == Side.WHITE) {
            alpha = Math.max(alpha, bestMove.eval);
        } else {
            beta = Math.min(beta, bestMove.eval);
        }
        if (beta <= alpha) {
            numPruned++;
            return bestMove;
        }

        List<Move> captureMoves = board.pseudoLegalCaptures();
        captureMoves.removeIf((move) -> {
            return !board.isMoveLegal(move, false);
        });
        if (captureMoves.isEmpty()) {
            return bestMove;
        }

        for (Move move : captureMoves) {
            board.doMove(move);
            EvalMove newMove = new EvalMove(move, quiescenceSearch(board, alpha, beta).eval);
            board.undoMove();
            if (board.getSideToMove() == Side.WHITE) {
                bestMove = maxMove(bestMove, newMove);
                alpha = Math.max(alpha, newMove.eval);
            } else {
                bestMove = minMove(bestMove, newMove);
                beta = Math.min(beta, newMove.eval);
            }
            if (beta <= alpha) {
                return bestMove;
            }

        }
        return bestMove;
    }

    // Finds the best move by searching all legal moves down to a specified depth
    public EvalMove minmax(Board board, int depth, int alpha, int beta) {
        nodesSearched++;
        int turn = board.getSideToMove() == Side.WHITE ? 1 : -1;
        if (depth == 0) return quiescenceSearch(board, alpha, beta);
        List<Move> legalMoves = board.legalMoves();
        if (board.isMated()) {
            return new EvalMove((9999 + depth * 100) * turn);
        } else if (board.isDraw()){
            return new EvalMove(0);
        }

        EvalMove bestMove;
        if (turn > 0) {
            bestMove = new EvalMove(Integer.MIN_VALUE);
        } else {
            bestMove = new EvalMove(Integer.MAX_VALUE);
        }
        // Goes through each move trying to find the best move among them
        for (Move move : legalMoves) {
            board.doMove(move);
            EvalMove newMove = new EvalMove(move, minmax(board, depth - 1, alpha, beta).eval);
            board.undoMove();
            // Compares and prunes the moves
            if (turn > 0) {
                bestMove = maxMove(bestMove, newMove);
                alpha = Math.max(alpha, newMove.eval);
            } else {
                bestMove = minMove(bestMove, newMove);
                beta = Math.min(beta, newMove.eval);
            }
            if (beta <= alpha) {
                numPruned++;
                return bestMove;
            }
        }
        return bestMove;
    }

    // Searches the opening database to find a move to make
//    public Move findMove(Board board) {
//        Move bestMove;
//        int color = board.getTurn();
//        boolean foundMove = false;
//        HashSet<Move> moves = new HashSet<>(board.allLegalMoves(color));
//        // Makes each move and tries to find a match in the database
//        if (color > 0) {
//            bestMove = new Move(-1, -1, -Double.MAX_VALUE);
//            for (Move move : moves) {
//                int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
//                String fen = board.toFEN();
//                if (Chess.evaluationData.containsKey(fen)) {
//                    move.setEvaluation(Chess.evaluationData.get(fen) / 100.0);
//                    bestMove = maxMove(move, bestMove);
//                    foundMove = true;
//                }
//                board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);
//            }
//        } else {
//            bestMove = new Move(-1, -1, Double.MAX_VALUE);
//            for (Move move : moves) {
//                int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
//                String fen = board.toFEN();
//                if (Chess.evaluationData.containsKey(fen)) {
//                    move.setEvaluation(Chess.evaluationData.get(fen) / 100.0);
//                    bestMove = minMove(move, bestMove);
//                    foundMove = true;
//                }
//                board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);
//            }
//        }
//        // Calls minmax to find a move if no moves are found
//        if (!foundMove) {
//            int depth;
//            // Searches to a deeper depth if there are fewer pieces on the board
//            if (board.getPieces().size() < 6) {
//                depth = 5;
//            } else {
//                depth = 3;
//            }
//            return minmax(board, depth, color, -Double.MAX_VALUE, Double.MAX_VALUE);
//        } else {
//            return bestMove;
//        }
//    }
}