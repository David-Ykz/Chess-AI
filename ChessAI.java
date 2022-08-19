import java.util.*;

class ChessAI {
    public static HashMap<HashMap<Integer, Integer>, Double> transpositions = new HashMap<>();
    public static int numTranspositions = 0;
    public static int numQuiescenceSearches = 0;

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

    public Move quiescenceSearch(Board board, boolean isMaximizingPlayer, double alpha, double beta) {
        int turn;
        if (isMaximizingPlayer) {
            turn = 1;
        } else {
            turn = -1;
        }
        HashSet<Move> moves = new HashSet<>(board.allCaptureMoves(turn));
        Move bestMove = new Move(-1, -1, board.evaluateBoard());
        if (isMaximizingPlayer) {
            for (Move move : moves) {
                int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
                Move newMove;
                newMove = new Move(move, quiescenceSearch(board, false, alpha, beta).getEvaluation());
                board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);
                bestMove = maxMove(bestMove, newMove);
                alpha = Math.max(alpha, newMove.getEvaluation());
                if (beta <= alpha) {
                    return bestMove;
                }
            }
            return bestMove;
        } else {
            for (Move move : moves) {
                int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
                Move newMove;
                newMove = new Move(move, quiescenceSearch(board, true, alpha, beta).getEvaluation());
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

    public Move minmax(Board board, int depth, boolean isMaximizingPlayer, double alpha, double beta) {
        if (depth == 0) {
            numQuiescenceSearches++;
            return quiescenceSearch(board, isMaximizingPlayer, alpha, beta);
//            if (Chess.numPieces - board.getPieces().size() > 2) {
//                numQuiescenceSearches++;
//                return quiescenceSearch(board, isMaximizingPlayer, alpha, beta);
//            } else {
//                return new Move(-1, -1, board.evaluateBoard());
//            }
        }

        int turn;
        if (isMaximizingPlayer) {
            turn = 1;
        } else {
            turn = -1;
        }
        HashSet<Move> moves = new HashSet<>(board.allLegalMoves(turn));
        if (isMaximizingPlayer) {
            Move bestMove = new Move(-1, -1, -Double.MAX_VALUE);
            for (Move move : moves) {
                int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
                Move newMove;
//                newMove = new Move(move, minmax(board, depth - 1, false, alpha, beta).getEvaluation());
                if (transpositions.containsKey(board.getPieces())) {
                    newMove = new Move(move, transpositions.get(board.getPieces()));
                    numTranspositions++;
                } else {
                    newMove = new Move(move, minmax(board, depth - 1, false, alpha, beta).getEvaluation());
                    transpositions.put(new HashMap<>(board.getPieces()), newMove.getEvaluation());
                }
                board.revertMove(move.getOldPosition(), move.getNewPosition(), capturedPiece);

                bestMove = maxMove(bestMove, newMove);
                alpha = Math.max(alpha, newMove.getEvaluation());
                if (beta <= alpha) {
                    return bestMove;
                }
            }
            return bestMove;
        } else {
            Move bestMove = new Move(-1, -1, Double.MAX_VALUE);
            for (Move move : moves) {
                int capturedPiece = board.movePiece(move.getOldPosition(), move.getNewPosition());
                Move newMove;
//                newMove = new Move(move, minmax(board, depth - 1, true, alpha, beta).getEvaluation());
                if (transpositions.containsKey(board.getPieces())) {
                    newMove = new Move(move, transpositions.get(board.getPieces()));
                    numTranspositions++;
                } else {
                    newMove = new Move(move, minmax(board, depth - 1, true, alpha, beta).getEvaluation());
                    transpositions.put(new HashMap<>(board.getPieces()), newMove.getEvaluation());
                }
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
            return minmax(board, 4, color == 1, -Double.MAX_VALUE, Double.MAX_VALUE);
        } else {
            return bestMove;
        }
    }

}