/**
 * Class for the button that gives the player the white pieces
 * @author David Ye
 */

package com.example.chess_logic;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;

class WhiteButtonListener extends ButtonListener {
    JFrame parentFrame;
    WhiteButtonListener(JFrame parent) {
        super (parent);
        parentFrame = parent;
    }
    public void actionPerformed(ActionEvent event)  {
        parentFrame.dispose();
        new Chess(1);
    }
}