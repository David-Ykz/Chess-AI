/**
 * Button for enabling/disabling the database from loading
 * @author David Ye
 */

package com.example.chess_logic;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;

class DatabaseButtonListener extends ButtonListener {
    JFrame parentFrame;
    DatabaseButtonListener(JFrame parent) {
        super (parent);
        parentFrame = parent;
    }
    public void actionPerformed(ActionEvent event)  {
        OpeningMenu.loadDatabase = !OpeningMenu.loadDatabase;
    }
}