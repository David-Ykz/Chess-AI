/**
 * Class for the button that allows the player to select which piece they want to promote to
 * @author David Ye
 */

package com.example.chess_logic;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;

class PromotionButtonListener extends ButtonListener {
    JFrame parentFrame;
    int piece;
    int color;
    int oldPosition;
    int newPosition;
    PromotionButtonListener(JFrame parent, int piece, int color, int oldPosition, int newPosition) {
        super (parent);
        parentFrame = parent;
        this.piece = piece;
        this.color = color;
        this.oldPosition = oldPosition;
        this.newPosition = newPosition;
    }
    public void actionPerformed(ActionEvent event)  {
        parentFrame.dispose();
        Chess.processMove(Chess.currentBoard, oldPosition, newPosition, (piece + 10) * color);
    }
}