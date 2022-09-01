import java.awt.*;
import java.util.*;

class Chess {
    static Board currentBoard;
    static HashSet<Integer> selectedSquares = new HashSet<>();
    static int selectedPiecePosition;
    static ChessAI chessAI = new ChessAI();
    static HashMap<Integer, HashMap<String, Integer>> evaluationData;
    static int numPieces = 0;
    static double timeTaken = 0;
    static double trueEval = 0;
    static ChessVisualizer visualizer;
    static boolean isAIMove = false;
    static boolean drawnBoard = false;

    Chess(int color) {
//        EvaluationReader evaluationReader = new EvaluationReader("chessData.csv");
        //      evaluationData = evaluationReader.getEvaluations();
        //        currentBoard = fenToBoard("7k/8/8/8/8/8/1p6/7K w - - 0 1", color);
//        currentBoard = fenToBoard("7k/b6p/8/8/8/1R4RQ/5pRK/7N w - - 0 1", color);
        currentBoard = fenToBoard("7k/b6p/8/8/8/1R4RQ/5pRK/6BN w - - 0 1", color);
//        currentBoard = fenToBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", color);
//        currentBoard = fenToBoard("7k/8/8/8/8/8/p7/7K w - - 0 1");
//        currentBoard = fenToBoard("2r5/1r6/4k3/8/8/K7/8/8 w - - 0 1");
        System.out.println("Finished Reading");
        visualizer = new ChessVisualizer(currentBoard);
        if (color < 0) {
            isAIMove = true;
        }
    }

    public static Board fenToBoard(String fen, int playerColor) {
        HashMap<Integer, Integer> pieces = new HashMap<>();
        HashMap<String, Integer> nameToValue = new HashMap<>();
        nameToValue.put("K", 6);
        nameToValue.put("Q", 5);
        nameToValue.put("R", 4);
        nameToValue.put("N", 3);
        nameToValue.put("B", 2);
        nameToValue.put("P", 1);
        nameToValue.put("k", -6);
        nameToValue.put("q", -5);
        nameToValue.put("r", -4);
        nameToValue.put("n", -3);
        nameToValue.put("b", -2);
        nameToValue.put("p", -1);
        for (int i = 0; i < 8; i++) {
            String row;
            int buffer = 0;
            if (i < 7) {
                row = fen.substring(0, fen.indexOf('/'));
                fen = fen.substring(fen.indexOf('/') + 1);
            } else {
                row = fen.substring(0, fen.indexOf(' '));
                fen = fen.substring(fen.indexOf(' ') + 1);
            }

            int rowIndex = 0;
            for (int j = 0; j < 8; j++) {
                rowIndex++;
                if (buffer == 0) {
                    String character = row.substring(0, 1);
                    row = row.substring(1);
                    if (nameToValue.containsKey(character)) {
                        pieces.put(10 * rowIndex + i + 1, nameToValue.get(character));
                    } else {
                        buffer = Integer.parseInt(character) - 1;
                    }
                } else {
                    buffer--;
                }
            }
        }
        int turn;
        if (fen.substring(0, 1).equals("w")) {
            turn = 1;
        } else {
            turn = -1;
        }
        return new Board(turn, pieces, playerColor);
    }
    public static void makeAIMove() {
        chessAI.resetStatistics();
        numPieces = currentBoard.getPieces().size();
        double start = System.nanoTime();
        int depth;
        if (currentBoard.getPieces().size() < 6) {
            depth = 7;
        } else {
            depth = 5;
        }
        Move aiMove = chessAI.minmax(currentBoard, depth, currentBoard.getTurn(), -Double.MAX_VALUE, Double.MAX_VALUE);
//        Move bestMove = chessAI.findMove(currentBoard);
        if (aiMove.getOldPosition() != -1) {
            currentBoard.makeMove(aiMove);
        }
        double end = System.nanoTime();
        timeTaken = (end - start)/1000000000;
        trueEval = Math.round(1000 * aiMove.getEvaluation())/1000.0;
        isAIMove = false;
        drawnBoard = false;
    }


    public static void processClick(int position, Board board) {
        boolean foundPiece = false;
        if (selectedSquares.contains(position)) {
            if (currentBoard.getPlayerColor() < 0) {
                position = (9 - position/10) * 10 + 9 - position%10;
            }
            Move playerMove = new Move(selectedPiecePosition, position);
            board.makeMove(playerMove);
            isAIMove = true;
        } else {
            if (currentBoard.getPlayerColor() < 0) {
                position = (9 - position/10) * 10 + 9 - position%10;
            }
            for (Integer piecePosition : board.getPieces().keySet()) {
                if (piecePosition == position && board.pieceColor(piecePosition) == board.getTurn()) {
                    selectedPiecePosition = position;
                    displaySelectedMoves(selectedPiecePosition, board);
                    foundPiece = true;
                }
            }
        }
        if (!foundPiece) {
            selectedSquares.clear();
            selectedPiecePosition = -1;
        }
    }


    public static void displaySelectedMoves(int position, Board board) {
        selectedSquares.clear();
        HashSet<Integer> legalMoves = board.findLegalMoves(position);
        for (Integer move : legalMoves) {
            if (board.getPlayerColor() > 0) {
                selectedSquares.add(move);
            } else {
                selectedSquares.add((9 - move/10) * 10 + 9 - move%10);
            }
        }
    }

    public static void displayInfo(Graphics g, int boardX, int boardY) {
        int textSize = 24;
        int offSet = 30;
        g.setFont (new Font ("SansSerif", Font.PLAIN, textSize - 4));
        g.setColor(Colors.textRed);
        g.drawString("Time taken: " + timeTaken, boardX, offSet);
        g.setColor(Colors.textOrange);
        g.drawString("Number of positions searched: " + ChessAI.positionsSearched, boardX, offSet + textSize);
        g.setColor(Colors.textGreen);
        g.drawString("Number of captures searched: " + ChessAI.captureSearches, boardX, offSet + 2 * textSize);
        g.setColor(Colors.textBlue);
        g.drawString("Number of checkmates found: " + ChessAI.checkmatesFound, boardX, offSet + 3 * textSize);
        g.setColor(Colors.textPurple);
        g.drawString("Evaluation: " + trueEval, boardX, offSet + 4 * textSize);
    }


    public static void main(String[] args) throws Exception {
//        EvaluationReader evaluationReader = new EvaluationReader("chessData.csv");
  //      evaluationData = evaluationReader.getEvaluations();
        currentBoard = fenToBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", 1);
//        currentBoard = fenToBoard("7k/8/8/8/8/8/p7/7K w - - 0 1");
//        currentBoard = fenToBoard("2r5/1r6/4k3/8/8/K7/8/8 w - - 0 1");
        System.out.println("Finished Reading");
        visualizer = new ChessVisualizer(currentBoard);
    }
}