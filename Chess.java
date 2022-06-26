import java.util.*;

class Chess {
    static Board currentBoard;
    static HashSet<Integer> selectedSquares = new HashSet<>();
    static int selectedPiecePosition;
//    static ChessAI chessAI = new ChessAI();
    static HashMap<Integer, HashMap<String, Integer>> evaluationData;

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
            for (int j = 0; j < row.length(); j++) {
                if (nameToValue.containsKey(row.substring(j, j + 1))) {
                    pieces.put(10 * (j + 1) + i + 1 + buffer * 10, nameToValue.get(row.substring(j, j + 1)));
                } else {
                    buffer = Integer.parseInt(row.substring(j, j + 1)) - 1;
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



//    public static void makeAIMove() {
//        double start = System.nanoTime();
//        Move bestMove = chessAI.generateDepthSearch(currentBoard, 3);
//        currentBoard.movePiece(currentBoard.getPiece(bestMove.getOldPosition()), bestMove.getNewPosition());
//        double end = System.nanoTime();
//        System.out.println((end - start)/1000000000);
//    }


    public static void processClick(int position, Board board) {
        boolean foundPiece = false;
        if (selectedSquares.contains(position)) {
//            if (selectedPiece.getName().compareTo("rook") == 0) {
//                ((Rook) selectedPiece).setMoved();
//            } else if (selectedPiece.getName().compareTo("king") == 0) {
//                ((King) selectedPiece).setMoved();
//            }
            board.movePiece(selectedPiecePosition, position);
            board.checkPromotion();
            board.changeTurn();

            if (board.isCheckmate()) {
                System.exit(0);
            }
//            int currentBoardTurn = board.getTurn();
//            makeAIMove();
//            chessAI.findMove(board); // Uses database

//            if (currentBoardTurn == board.getTurn()) {
 //               board.changeTurn();
  //          }
//            board.changeTurn();
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
//        HashMap<Integer, Piece> startingPieces = fillStartingPieces();
//        HashMap<Integer, Piece> startingPieces = setupCustomBoard();
        currentBoard = fenToBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
//        HashMap<Integer, Integer> somePieces = new HashMap<>();
 //       somePieces.put(11, 4);
  //      somePieces.put(54, 4);
    //    currentBoard = new Board(1, somePieces);
        ChessVisualizer visualizer = new ChessVisualizer(currentBoard);
//        System.out.println(currentBoard.toFEN());

//    for (int i = 0; i < currentBoard.getPieces().size(); i++) {
//      selectedSquares.addAll(currentBoard.getPiece(i).findPossibleMoves(currentBoard));
//    }
    }
}