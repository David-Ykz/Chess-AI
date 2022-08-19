import java.util.*;

class ChessAI {
    public static HashMap<HashMap<Integer, Integer>, Double> transpositions = new HashMap<>();
    public static int numTranspositions = 0;
    public static int numQuiescenceSearches = 0;
    public static int positionsSearched = 0;
    public static int captureSearches = 0;

    ChessAI() {
    }
    public void resetStatistics() {
        transpositions.clear();
        numTranspositions = 0;
        numQuiescenceSearches = 0;
        positionsSearched = 0;
        captureSearches = 0;
    }
    public void printInfo() {
        System.out.println("Number of transpositions: " + numTranspositions);
        System.out.println("Number of quiescence searches: " + numQuiescenceSearches);
        System.out.println("Number of positions searched: " + positionsSearched);
        System.out.println("Number of captures searched: " + captureSearches);
    }
    public Move maxMove(Move a, Move b) {
        if (a.getEvaluation() >= b.getEvaluation()) {
            return a;
        } else {
            return b;
        }
    }
    public Move minMove(Move a, Move b) {
        if (a.getEvaluation() <= b.getEvaluation()) {
            return a;
        } else {
            return b;
        }
    }
    public ArrayList<Move> orderMoves(Board board, HashSet<Move> moves) {
        ArrayList<Move> orderedMoves = new ArrayList<>(moves.size());

        for (Move move : moves) {
            int moveScore = 0;
            int piece = board.getPieces().get(move.getOldPosition()) % 10;
            if (board.getPieces().containsKey(move.getNewPosition())) {
                int capturedPiece = board.getPieces().get(move.getNewPosition());
                moveScore = 10 * (int)board.pieceValues[Math.abs(capturedPiece % 10) - 1] - (int)board.pieceValues[Math.abs(piece % 10) - 1];
            }
            move.setMoveScore(moveScore);
            orderedMoves.add(move);
        }
        Collections.sort(orderedMoves);
        return orderedMoves;
    }

    public Move quiescenceSearch(Board board, int turn, double alpha, double beta) {
        captureSearches++;

        Move bestMove = new Move(-1, -1, board.basicEvaluation());
        if (turn > 0) {
            alpha = Math.max(alpha, bestMove.getEvaluation());
        } else {
            beta = Math.min(beta, bestMove.getEvaluation());
        }
        if (beta <= alpha) {
            return bestMove;
        }


        HashSet<Move> unsortedMoves = new HashSet<>(board.allCaptureMoves(turn));
        if (unsortedMoves.size() == 0) { // If the turn player has no moves to make
            if (board.findAllPossibleMoves(-turn).contains(board.findKingPos(turn))) { // Checkmate
                return new Move(-1, -1, Double.MAX_VALUE * -turn);
            } else { // Stalemate
                return new Move(-1, -1, 0);
            }
        }
        ArrayList<Move> moves = orderMoves(board, unsortedMoves);

        for (Move move : moves) {
            int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
            Move newMove = new Move(move, quiescenceSearch(board, -turn, alpha, beta).getEvaluation());
            board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);
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
    public Move minmax(Board board, int depth, int turn, double alpha, double beta) {
        positionsSearched++;
        if (depth == 0) {
            numQuiescenceSearches++;
            return quiescenceSearch(board, turn, alpha, beta);
        }

        HashSet<Move> unsortedMoves = new HashSet<>(board.allLegalMoves(turn));
        if (unsortedMoves.size() == 0) { // If the turn player has no moves to make
            if (board.findAllPossibleMoves(-turn).contains(board.findKingPos(turn))) { // Checkmate
                return new Move(-1, -1, Double.MAX_VALUE/2 * -turn);
            } else { // Stalemate
                return new Move(-1, -1, 0);
            }
        }
        ArrayList<Move> moves = orderMoves(board, unsortedMoves);

        Move bestMove = new Move(-1, -1, Double.MAX_VALUE * -turn);
        // Goes through each move trying to find the best move among them
        for (Move move : moves) {
            int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
            Move newMove;
            // If the position is not found in the transposition table, it will recursively call itself
            if (transpositions.containsKey(board.getPieces())) {
                newMove = new Move(move, transpositions.get(board.getPieces()));
                numTranspositions++;
            } else {
                newMove = new Move(move, minmax(board, depth - 1, -turn, alpha, beta).getEvaluation());
                transpositions.put(new HashMap<>(board.getPieces()), newMove.getEvaluation());
            }
            board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);
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
    public Move findMove(Board board) {
        Move bestMove;
        int color = board.getTurn();
        boolean foundMove = false;
        HashSet<Move> moves = new HashSet<>(board.allLegalMoves(color));
        if (color > 0) {
            bestMove = new Move(-1, -1, -Double.MAX_VALUE);
            for (Move move : moves) {
                int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
                String fen = board.toFEN();
                System.out.println(fen);
                if (Chess.evaluationData.containsKey(board.getPieces().size()) && Chess.evaluationData.get(board.getPieces().size()).containsKey(fen)) {
                    move.setEvaluation(Chess.evaluationData.get(board.getPieces().size()).get(fen));
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
                if (Chess.evaluationData.containsKey(board.getPieces().size()) && Chess.evaluationData.get(board.getPieces().size()).containsKey(fen)) {
                    move.setEvaluation(Chess.evaluationData.get(board.getPieces().size()).get(fen));
                    bestMove = minMove(move, bestMove);
                    foundMove = true;
                }
                board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);
            }
        }
        if (!foundMove) {
            return minmax(board, 4, color, -Double.MAX_VALUE, Double.MAX_VALUE);
        } else {
            return bestMove;
        }
    }

}