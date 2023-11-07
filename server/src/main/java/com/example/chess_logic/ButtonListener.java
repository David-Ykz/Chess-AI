/**
 * Abstract class for buttons and the action performed upon being pressed
 * @author David Ye
 */

package com.example.chess_logic;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

abstract class ButtonListener implements ActionListener {
    JFrame parentFrame;
    ButtonListener(JFrame parent) {
        parentFrame = parent;
    }
    // The action performed will vary based on what button the user clicks
    public abstract void actionPerformed(ActionEvent event);
}