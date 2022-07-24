import java.util.*;

class ChessAI {
    public static int[] depthMap = new int[4];

    ChessAI() {
    }
    public Move maxMove(Move a, Move b) {
        if (a.getEvaluation() > b.getEvaluation()) {
            return a;
        } else {
            return b;
        }
    }

    public Move minMove(Move a, Move b) {
        if (a.getEvaluation() < b.getEvaluation()) {
            return a;
        } else {
            return b;
        }
    }

    public HashSet<Move> generatePositionMoves(Board board, int color) {
        System.out.println("generating");
        HashSet<Move> moves = new HashSet<>();
        for (Integer oldPosition : board.getPieces().keySet()) {
            if (board.pieceColor(oldPosition) == color) { // Finds all pieces of the turn player
                for (int eachMove : board.findLegalMoves(oldPosition)) {
                    moves.add(new Move(oldPosition, eachMove));
                }
            }
        }

        return moves;
    }

    public Move minmax(Board board, int depth, boolean isMaximizingPlayer, double alpha, double beta) {
        depthMap[depth] += 1;
        System.out.println("Depth 0 Check");
        if (depth == 0) {
            System.out.println("Returned");
            return new Move(-1, -1, board.evaluateBoard());
        }

        System.out.println(isMaximizingPlayer);

        if (isMaximizingPlayer) {
            System.out.println("Maximizing Player");
            Move bestMove = new Move(-1, -1, -Board.INFINITY);
            System.out.println("Move Generation");
            for (Move move : generatePositionMoves(board, 1)) {
                int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
                Move newMove = new Move(move, minmax(board, depth - 1, false, alpha, beta).getEvaluation());
                System.out.println("Reverting Move");
                board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);

                bestMove = maxMove(bestMove, newMove);
                alpha = Math.max(alpha, newMove.getEvaluation());
                if (beta <= alpha) {
                    return bestMove;
                }
            }
            System.out.println("Returning Max");
            return bestMove;
        } else {
            System.out.println("Minimizing Player");
            Move bestMove = new Move(-1, -1, Board.INFINITY);
            System.out.println("Move Generation");
            for (Move move : generatePositionMoves(board, -1)) {
                System.out.println("In for loop");
                int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
                Move newMove = new Move(move, minmax(board, depth - 1, true, alpha, beta).getEvaluation());
                System.out.println("Reverting Move");
                board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);

                bestMove = minMove(bestMove, newMove);
                beta = Math.min(beta, newMove.getEvaluation());
                if (beta <= alpha) {
                    return bestMove;
                }
            }
            System.out.println("Returning Min");
            return bestMove;
        }
    }
}