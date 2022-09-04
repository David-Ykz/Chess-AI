/**
 * Main class for managing all elements of the program
 * @author David Ye
 */

import java.awt.*;
import java.util.*;

class Chess {
    static Board currentBoard;
    static HashSet<Integer> selectedSquares = new HashSet<>();
    static int selectedPiecePosition;
    static ChessAI chessAI = new ChessAI();
    static HashMap<String, Integer> evaluationData = new HashMap<>();
    static int numPieces = 0;
    static double timeTaken = 0;
    static double trueEval = 0;
    static ChessVisualizer visualizer;
    static boolean isAIMove = false;
    static boolean drawnBoard = false;

    Chess(int color) {
        // Loads the board with the starting position
        currentBoard = fenToBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", color);
        if (OpeningMenu.loadDatabase) {
            double start = System.nanoTime();
            // Reads an opening database
            EvaluationReader evaluationReader = new EvaluationReader("chessData.csv");
            evaluationData = evaluationReader.getEvaluations();
            double end = System.nanoTime();
            System.out.println("Finished Reading");
            System.out.println("Number of positions: " + evaluationData.size());
            System.out.println("Time taken: " + (end - start)/1000000000);
        }
        // Initializes the graphical display
        visualizer = new ChessVisualizer(currentBoard);
        if (color < 0) {
            isAIMove = true;
        }
    }

    // Converts fen strings to a board
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
        // Iterates through the fen string and places pieces into a hashmap
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

    // Starts a move search for the Chess AI
    public static void makeAIMove() {
        chessAI.resetStatistics();
        numPieces = currentBoard.getPieces().size();
        double start = System.nanoTime();
        Move aiMove = chessAI.findMove(currentBoard);
        if (aiMove.getOldPosition() != -1) {
            currentBoard.makeMove(aiMove);
        }
        double end = System.nanoTime();
        timeTaken = (end - start)/1000000000;
        trueEval = Math.round(1000 * aiMove.getEvaluation())/1000.0;
        isAIMove = false;
        drawnBoard = false;
    }

    // Processes any clicks performed by the user (either moving a piece or showing the moves a piece can make)
    public static void processClick(int position, Board board) {
        boolean foundPiece = false;
        // Moves the piece
        if (selectedSquares.contains(position)) {
            if (currentBoard.getPlayerColor() < 0) {
                position = (9 - position/10) * 10 + 9 - position%10;
            }
            // Checks for promotions, and if there are any, opens up a separate window
            if (board.checkPromotion(selectedPiecePosition, position)) {
                PromotionMenu promotionMenu = new PromotionMenu(board.getTurn(), selectedPiecePosition, position);
            } else {
                processMove(board, selectedPiecePosition, position, 0);
            }
        } else {
            // Displays the squares a selected piece can be moved to
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

    // Moves the piece on the board and calls the AI to move
    public static void processMove(Board board, int oldPosition, int newPosition, int promotedPiece) {
        Move playerMove;
        if (promotedPiece != 0) {
            playerMove = new Move(oldPosition, newPosition, promotedPiece);
        } else {
            playerMove = new Move(oldPosition, newPosition);
        }
        board.makeMove(playerMove);
        isAIMove = true;
    }

    // Adds legal moves to be displayed on screen
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

    // Displays information about the board and chess AI on the screen
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
}