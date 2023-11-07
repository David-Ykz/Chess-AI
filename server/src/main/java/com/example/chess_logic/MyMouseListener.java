/**
 * Listeners for any player inputs and transmits them to be processed
 * @author David Ye
 */

package com.example.chess_logic;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

class MyMouseListener implements MouseListener {
    public void mousePressed(MouseEvent e) {
        // If the left mouse button is pressed
        if (e.getButton() == 1) {
            int xSquare = e.getX()/ChessVisualizer.GRIDSIZE + 1;
            int ySquare = (e.getY() - 30)/ChessVisualizer.GRIDSIZE + 1;
            Chess.processClick(xSquare * 10 + ySquare, Chess.currentBoard);
        }
    }
    public void mouseClicked(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}