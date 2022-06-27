import java.util.*;

class ChessAI {
    ChessAI() {
    }

    // Find all possible moves
    // Convert to fen
    // Give evaluations for each move
    // Pick the best evaluation


//    public void findMove(Board board) {
//        ArrayList<Move> moves = new ArrayList<>();
//        Chess.checkPromotion(board);
//        int color = board.getTurn();
//        for (Piece piece : board.getPieces()) {
//            if (piece.getColor() == color) {
//                for (int eachMove : piece.findLegalMoves(board)) {
//                    int oldPosition = piece.getPosition();
//                    Piece capturedPiece = board.movePiece(piece, eachMove);
//                    String fen = board.toFEN();
//                    int evaluation;
//                    if (Chess.evaluationData.containsKey(board.getPieces().size()) && Chess.evaluationData.get(board.getPieces().size()).containsKey(fen)) {
//                        evaluation = Chess.evaluationData.get(board.getPieces().size()).get(fen);
//                        Move move = new Move(oldPosition, piece.getPosition(), evaluation);
//                        moves.add(move);
//                    }
//                    board.revertMove(piece, capturedPiece, oldPosition);
//                }
//            }
//        }
//        Move bestMove;
//        if (moves.size() == 0) {
//            bestMove = generateDepthSearch(board, 3);
//        } else if (board.getTurn() > 0) {
//            bestMove = max(moves);
//        } else {
//            bestMove = min(moves);
//        }
//        board.movePiece(board.getPiece(bestMove.getOldPosition()), bestMove.getNewPosition());
//    }

    public Move max(Move a, Move b) {
        if (a.getEvaluation() > b.getEvaluation()) {
            return a;
        } else {
            return b;
        }
    }

    public Move min(Move a, Move b) {
        if (a.getEvaluation() < b.getEvaluation()) {
            return a;
        } else {
            return b;
        }
    }

    public Move max(ArrayList<Move> moves) {
        Move bestMove = moves.get(0);
        for (Move move : moves) {
            if (move.getEvaluation() > bestMove.getEvaluation()) {
                bestMove = move;
            }
        }
        return bestMove;
    }

    public Move min(ArrayList<Move> moves) {
        Move bestMove = moves.get(0);
        for (Move move : moves) {
            if (move.getEvaluation() < bestMove.getEvaluation()) {
                bestMove = move;
            }
        }
        return bestMove;
    }

    public Move minmax(Board board, Move move, int depth, int color, double alpha, double beta) {
        if (depth == 0) { // ?????????????????????????????????????????????????????????????????????????????????????????????
            return move;
        }

        if (color > 0) {
            Move bestValue = new Move(-1, -1, -Double.MAX_VALUE);
            outside:
            for (Integer oldPosition : board.getPieces().keySet()) {
                if (board.pieceColor(oldPosition) == color) { // Finds all pieces of the turn player
                    for (int eachMove : board.findLegalMoves(oldPosition)) {
                        int capturedPiece = board.movePiece(oldPosition, eachMove);
                        Move responseMove = new Move(oldPosition, eachMove, board.evaluateBoard());
                        Move newMove = minmax(board, responseMove, depth - 1, color * -1, alpha, beta);
                        board.revertMove(oldPosition, eachMove, capturedPiece);
                        bestValue = max(bestValue, newMove);
                        alpha = Math.max(alpha, bestValue.getEvaluation());
                        if (beta <= alpha) {
                            break outside;
                        }
                    }
                }
            }
            return bestValue;
        } else {
            Move bestValue = new Move(-1, -1, Double.MAX_VALUE);
            outside:
            for (Integer oldPosition : board.getPieces().keySet()) {
                if (board.pieceColor(oldPosition) == color) { // Finds all pieces of the turn player
                    for (int eachMove : board.findLegalMoves(oldPosition)) {
                        int capturedPiece = board.movePiece(oldPosition, eachMove);
                        Move responseMove = new Move(oldPosition, eachMove, board.evaluateBoard());
                        Move newMove = minmax(board, responseMove, depth - 1, color * -1, alpha, beta);
                        board.revertMove(oldPosition, eachMove, capturedPiece);
                        bestValue = min(bestValue, newMove);
                        beta = Math.min(beta, bestValue.getEvaluation());
                        if (beta <= alpha) {
                            break outside;
                        }
                    }
                }
            }
            return bestValue;

        }
    }

    public Move generateDepthSearch(Board board, int depth, int color, double alpha, double beta) {
        ArrayList<Move> moves = new ArrayList<>();
        Move bestMove;
        for (Integer oldPosition : board.getPieces().keySet()) {
            if (board.pieceColor(oldPosition) == color) { // Finds all pieces of the turn player
                for (int eachMove : board.findLegalMoves(oldPosition)) {
                    int capturedPiece = board.movePiece(oldPosition, eachMove);
                    if (depth == 0) {
                        moves.add(new Move(oldPosition, eachMove, board.evaluateBoard()));
                    } else {
                        bestMove = generateDepthSearch(board, depth - 1, color * -1, alpha, beta);
                        moves.add(new Move(oldPosition, eachMove, bestMove.getEvaluation()));
                    }
                    board.revertMove(oldPosition, eachMove, capturedPiece);
                }
            }
        }
        if (moves.size() == 0) {
            return new Move(-1, -1, -9999 * color);
        }
        if (color > 0) {
            return max(moves);
        } else {
            return min(moves);
        }
    }
}