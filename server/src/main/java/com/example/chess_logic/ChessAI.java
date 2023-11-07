/**
 * Finds the most optimal moves to play in a situration
 * @author David Ye
 */

package com.example.chess_logic;
import java.util.*;

public class ChessAI {
    // Statistics displayed to the user
    public static int numQuiescenceSearches = 0;
    public static int positionsSearched = 0;
    public static int captureSearches = 0;
    public static int positionsPruned = 0;
    public static int checkmatesFound = 0;

    public ChessAI() {
    }
    public void resetStatistics() {
        numQuiescenceSearches = 0;
        positionsSearched = 0;
        captureSearches = 0;
        positionsPruned = 0;
        checkmatesFound = 0;
    }
    public void printInfo() {
        System.out.println("Number of quiescence searches: " + numQuiescenceSearches);
        System.out.println("Number of positions searched: " + positionsSearched);
        System.out.println("Number of captures searched: " + captureSearches);
        System.out.println("Number of positions pruned: " + positionsPruned);
        System.out.println("Number of checkmates found: " + checkmatesFound);
    }

    // Find the move with the highest evaluation
    public Move maxMove(Move a, Move b) {
        if (a.getEvaluation() >= b.getEvaluation()) {
            return a;
        } else {
            return b;
        }
    }
    // Finds the move with the lowest evaluation
    public Move minMove(Move a, Move b) {
        if (a.getEvaluation() <= b.getEvaluation()) {
            return a;
        } else {
            return b;
        }
    }

    // Sorts the moves so better moves are searched first
    public ArrayList<Move> orderMoves(Board board, HashSet<Move> moves, int color) {
        ArrayList<Move> orderedMoves = new ArrayList<>(moves.size());
        for (Move move : moves) {
            int moveScore = 0;
            // Assigns a move score to each move based on a guess for how good the move is
            int piece = board.getPieces().get(move.getOldPosition()) % 10;
            if (board.getPieces().containsKey(move.getNewPosition())) {
                int capturedPiece = board.getPieces().get(move.getNewPosition());
                // Capturing a piece with a piece of less value is a bonus
                moveScore += 10 * (int)board.pieceValues[Math.abs(capturedPiece % 10) - 1] - (int)board.pieceValues[Math.abs(piece % 10) - 1];
                // An exception is made for the king, as any legal capture with the king is good
                if (Math.abs(piece) % 10 == 6) {
                    moveScore += (int)board.pieceValues[Math.abs(piece % 10) - 1];
                }
            }
            // Discourages moving to a square attacked by an enemy pawn
            if (board.allPawnSquares(-color).contains(move.getNewPosition())) {
                moveScore -= 1;
            }
            // Encourages promoting pawns
            moveScore += Math.abs(move.getPromotedPiece() % 10);
            move.setMoveScore(moveScore);
            orderedMoves.add(move);
        }
        // Sorts the moves so moves with higher scores are at the beginning of the arraylist
        Collections.sort(orderedMoves);
        return orderedMoves;
    }

    // Searches all captures until no captures can be made or all captures put the player in a worse state
    public Move quiescenceSearch(Board board, int turn, double alpha, double beta) {
        captureSearches++;
        // Represents the option to not make a capture
        Move bestMove = new Move(-1, -1, board.evaluateBoard());
        // Prunes the move
        if (turn > 0) {
            alpha = Math.max(alpha, bestMove.getEvaluation());
        } else {
            beta = Math.min(beta, bestMove.getEvaluation());
        }
        if (beta <= alpha) {
            positionsPruned++;
            return bestMove;
        }

        // Finds and sorts all moves that result in a capture
        HashSet<Move> unsortedMoves = new HashSet<>(board.allCaptureMoves(turn));
        if (unsortedMoves.size() == 0) { // If the turn player has no captures to make
            return bestMove;
        }
        ArrayList<Move> moves = orderMoves(board, unsortedMoves, turn);

        // Iterates through each move, recursively calling itself
        for (Move move : moves) {
            int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
            Move newMove = new Move(move, quiescenceSearch(board, -turn, alpha, beta).getEvaluation());
            board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);
            // Compares the capture move with the option to not capture
            if (turn > 0) {
                bestMove = maxMove(bestMove, newMove);
                alpha = Math.max(alpha, newMove.getEvaluation());
            } else {
                bestMove = minMove(bestMove, newMove);
                beta = Math.min(beta, newMove.getEvaluation());
            }
            if (beta <= alpha) {
                return bestMove;
            }
        }
        return bestMove;
    }

    // Finds the best move by searching all legal moves down to a specified depth
    public Move minmax(Board board, int depth, int turn, double alpha, double beta) {
        positionsSearched++;
        // Searches all captures upon reaching the lowest depth
        if (depth == 0) {
            numQuiescenceSearches++;
            return quiescenceSearch(board, turn, alpha, beta);
        }
        HashSet<Move> unsortedMoves = new HashSet<>(board.allLegalMoves(turn));
        if (unsortedMoves.size() == 0) { // If the turn player has no moves to make
            if (board.findAllPossibleMoves(-turn).contains(board.findKingPos(turn))) { // Checkmate
                checkmatesFound++;
                // Encourages the moves that lead to the fastest checkmate
                return new Move(-1, -1, (99999 + depth * 100) * -turn * 1.0);
            } else { // Stalemate
                return new Move(-1, -1, 0.0);
            }
        }
        ArrayList<Move> moves = orderMoves(board, unsortedMoves, turn);

        Move bestMove = new Move(-1, -1, Double.MAX_VALUE * -turn);
        // Goes through each move trying to find the best move among them
        for (Move move : moves) {
            Move newMove;
            int capturedPiece = board.movePiece(move);
            newMove = new Move(move, minmax(board, depth - 1, -turn, alpha, beta).getEvaluation());
            board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);
            // Compares and prunes the moves
            if (turn > 0) {
                bestMove = maxMove(bestMove, newMove);
                alpha = Math.max(alpha, newMove.getEvaluation());
            } else {
                bestMove = minMove(bestMove, newMove);
                beta = Math.min(beta, newMove.getEvaluation());
            }
            if (beta <= alpha) {
                positionsPruned++;
                return bestMove;
            }
        }
        return bestMove;
    }

    // Searches the opening database to find a move to make
    public Move findMove(Board board) {
        Move bestMove;
        int color = board.getTurn();
        boolean foundMove = false;
        HashSet<Move> moves = new HashSet<>(board.allLegalMoves(color));
        // Makes each move and tries to find a match in the database
        if (color > 0) {
            bestMove = new Move(-1, -1, -Double.MAX_VALUE);
            for (Move move : moves) {
                int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
                String fen = board.toFEN();
                if (Chess.evaluationData.containsKey(fen)) {
                    move.setEvaluation(Chess.evaluationData.get(fen) / 100.0);
                    bestMove = maxMove(move, bestMove);
                    foundMove = true;
                }
                board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);
            }
        } else {
            bestMove = new Move(-1, -1, Double.MAX_VALUE);
            for (Move move : moves) {
                int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
                String fen = board.toFEN();
                if (Chess.evaluationData.containsKey(fen)) {
                    move.setEvaluation(Chess.evaluationData.get(fen) / 100.0);
                    bestMove = minMove(move, bestMove);
                    foundMove = true;
                }
                board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);
            }
        }
        // Calls minmax to find a move if no moves are found
        if (!foundMove) {
            int depth;
            // Searches to a deeper depth if there are fewer pieces on the board
            if (board.getPieces().size() < 6) {
                depth = 7;
            } else {
                depth = 5;
            }
            return minmax(board, depth, color, -Double.MAX_VALUE, Double.MAX_VALUE);
        } else {
            return bestMove;
        }
    }
}





