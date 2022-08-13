import java.util.*;

class ChessAI {

    ChessAI() {
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

    public HashSet<Move> generatePositionMoves(Board board, int color) {
        HashSet<Move> moves = new HashSet<>();
        HashSet<Integer> piecePositions = new HashSet<>(board.getPieces().keySet());
        for (Integer oldPosition : piecePositions) {
            if (board.pieceColor(oldPosition) == color) { // Finds all pieces of the turn player
                for (int eachMove : board.findLegalMoves(oldPosition)) {
                    moves.add(new Move(oldPosition, eachMove));
                }
            }
        }

        return moves;
    }

    public Move minmax(Board board, int depth, boolean isMaximizingPlayer, double alpha, double beta) {
        if (depth == 0) {
            return new Move(-1, -1, board.evaluateBoard());
        }

        int turn;
        if (isMaximizingPlayer) {
            turn = 1;
        } else {
            turn = -1;
        }
        HashSet<Move> moves = new HashSet<>(generatePositionMoves(board, turn));
        if (isMaximizingPlayer) {
            Move bestMove = new Move(-1, -1, -Board.INFINITY);
            for (Move move : moves) {
                int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
                Move newMove = new Move(move, minmax(board, depth - 1, false, alpha, beta).getEvaluation());
                board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);

                bestMove = maxMove(bestMove, newMove);
                alpha = Math.max(alpha, newMove.getEvaluation());
                if (beta <= alpha) {
                    return bestMove;
                }
            }
            return bestMove;
        } else {
            Move bestMove = new Move(-1, -1, Board.INFINITY);
            for (Move move : moves) {
                int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
                Move newMove = new Move(move, minmax(board, depth - 1, true, alpha, beta).getEvaluation());
                board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);

                bestMove = minMove(bestMove, newMove);
                beta = Math.min(beta, newMove.getEvaluation());
                if (beta <= alpha) {
                    return bestMove;
                }
            }
            return bestMove;
        }
    }

}