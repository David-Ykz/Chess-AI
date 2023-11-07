/**
 * Graphical display of the board
 * @author David Ye
 */

package com.example.chess_logic;
import javax.swing.*;
import java.awt.*;

public class ChessVisualizer extends JFrame{
    GamePanel panel;
    Board board;
    final int MAX_X = (int)getToolkit().getScreenSize().getWidth();
    final int MAX_Y = (int)getToolkit().getScreenSize().getHeight();
    static final int GRIDSIZE = 67;
    final int BOARD_SIZE = GRIDSIZE * 8;
    ChessVisualizer(Board board) {
        MyMouseListener mouseListener = new MyMouseListener();
        this.addMouseListener(mouseListener);
        this.board = board;
        this.panel = new GamePanel();
        this.getContentPane().add(BorderLayout.CENTER, panel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(BOARD_SIZE + 400, BOARD_SIZE + 40);
        this.setResizable (false);
        this.setVisible(true);
    }
    private class GamePanel extends JPanel {
        @Override
        // Called every loop to redraw the screen
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Colors.background);
            g.fillRect(0, 0, MAX_X, MAX_Y);
            board.drawBoard(g, GRIDSIZE);
            Chess.displayInfo(g, BOARD_SIZE + 10, BOARD_SIZE + 10);
            if (board.isCheckmate()) {
                g.setFont (new Font ("SansSerif", Font.BOLD | Font.PLAIN, 65));
                g.setColor(Colors.textWhite);
                g.drawString("Checkmate!", BOARD_SIZE + 10, BOARD_SIZE - 20);
            }
            if (board.isStalemate()) {
                g.setFont (new Font ("SansSerif", Font.BOLD | Font.PLAIN, 65));
                g.setColor(Colors.textWhite);
                g.drawString("Stalemate!", BOARD_SIZE + 10, BOARD_SIZE - 20);
            }
            this.repaint();
            if (Chess.drawnBoard) {
                Chess.makeAIMove();
            }
            if (Chess.isAIMove) {
                Chess.drawnBoard = true;
            }
        }




    }
}