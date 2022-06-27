import java.util.*;

class Chess {
    static Board currentBoard;
    static HashSet<Integer> selectedSquares = new HashSet<>();
    static int selectedPiecePosition;
    static ChessAI chessAI = new ChessAI();
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
        double start = System.nanoTime();
//        Move bestMove = chessAI.generateDepthSearch(currentBoard, 3, currentBoard.getTurn());
        Move bestMove = chessAI.minmax(currentBoard, new Move(-1, -1, currentBoard.evaluateBoard()), 3, currentBoard.getTurn(), 0, 0);
        currentBoard.movePiece(bestMove.getOldPosition(), bestMove.getNewPosition());
        double end = System.nanoTime();
        System.out.println((end - start)/1000000000);
    }


    public static void processClick(int position, Board board) {
        boolean foundPiece = false;
        if (selectedSquares.contains(position)) {
            if (board.getPiece(selectedPiecePosition) == 4 && selectedPiecePosition == 18) {
                board.setCastleQW(false);
            } else if (board.getPiece(selectedPiecePosition) == 4 && selectedPiecePosition == 88) {
                board.setCastleKW(false);
            }
            if (board.getPiece(selectedPiecePosition) == -4 && selectedPiecePosition == 11) {
                board.setCastleQB(false);
            } else if (board.getPiece(selectedPiecePosition) == -4 && selectedPiecePosition == 81) {
                board.setCastleKB(false);
            }
            board.movePiece(selectedPiecePosition, position);
            board.checkPromotion();
            board.changeTurn();
            if (board.isCheckmate()) {
                System.exit(0);
            }
            int currentBoardTurn = board.getTurn();
            makeAIMove();
//            chessAI.findMove(board); // Uses database

            if (currentBoardTurn == board.getTurn()) {
                board.changeTurn();
            }
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
        currentBoard = fenToBoard("rnb1k2r/pppp1ppp/8/4N3/2B1n2q/8/PPPP2PP/RNBQK2R w kq - 2 7");
//        currentBoard = fenToBoard("rnbqk2r/pppp1ppp/5n2/2b1p3/2B1P3/5N2/PPPP1PPP/RNBQK2R w KQkq - 4 4");
//        currentBoard = fenToBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
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