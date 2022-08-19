import java.util.*;

class Chess {
    static Board currentBoard;
    static HashSet<Integer> selectedSquares = new HashSet<>();
    static int selectedPiecePosition;
    static ChessAI chessAI = new ChessAI();
    static HashMap<Integer, HashMap<String, Integer>> evaluationData;
    static int numPieces = 0;

    public static Board fenToBoard(String fen) {
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
                System.out.println(row);
                fen = fen.substring(fen.indexOf('/') + 1);
            } else {
                row = fen.substring(0, fen.indexOf(' '));
                System.out.println(row);
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
        int color;
        if (fen.substring(0, 1).equals("w")) {
            color = 1;
        } else {
            color = -1;
        }
        return new Board(color, pieces);
    }

    public static void makeAIMove() {
        ChessAI.transpositions.clear();
        ChessAI.numTranspositions = 0;
        ChessAI.numQuiescenceSearches = 0;
        numPieces = currentBoard.getPieces().size();
        double start = System.nanoTime();
        Move aiMove = chessAI.minmax(currentBoard, 3, currentBoard.getTurn() == 1, -Double.MAX_VALUE, Double.MAX_VALUE);
//        Move bestMove = chessAI.findMove(currentBoard);
        currentBoard.makeMove(aiMove);
        double end = System.nanoTime();
        System.out.println("---------------------");
        System.out.println("Time taken: " + (end - start)/1000000000);
        System.out.println("Number of transpositions: " + ChessAI.numTranspositions);
        System.out.println("Number of quiescence searches: " + ChessAI.numQuiescenceSearches);
        System.out.println("Evaluation: " + Math.round(1000 * currentBoard.evaluateBoard())/1000.0);
    }


    public static void processClick(int position, Board board) {
        boolean foundPiece = false;
        if (selectedSquares.contains(position)) {
            Move playerMove = new Move(selectedPiecePosition, position);
            board.makeMove(playerMove);
            board.checkPromotion();
            board.changeTurn();
            if (board.isCheckmate()) {
                System.exit(0);
            }
            int currentBoardTurn = board.getTurn();
            makeAIMove();

            if (currentBoardTurn == board.getTurn()) {
                board.changeTurn();
            }
        } else {
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
        selectedSquares.addAll(board.findLegalMoves(position));
    }

    public static void main(String[] args) throws Exception {
//        EvaluationReader evaluationReader = new EvaluationReader("chessData.csv");
  //      evaluationData = evaluationReader.getEvaluations();
        //      currentBoard = fenToBoard("6k1/pp6/4qp1r/2R3p1/bP2R1p1/4P1P1/5PBP/rQ4K1 w KQkq - 0 1");
        currentBoard = fenToBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
//        currentBoard = fenToBoard("2kr3r/pb1p3p/1bp1p3/4qp2/NP6/P2Q2P1/2P1BP1P/3RK2R b K - 8 21");
        System.out.println("Finished Reading");
        ChessVisualizer visualizer = new ChessVisualizer(currentBoard);
    }
}