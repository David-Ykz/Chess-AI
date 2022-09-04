/**
 * Class for the button that gives the player the black pieces
 * @author David Ye
 */

import javax.swing.JFrame;
import java.awt.event.ActionEvent;

class BlackButtonListener extends ButtonListener {
    JFrame parentFrame;
    BlackButtonListener(JFrame parent) {
        super (parent);
        parentFrame = parent;
    }
    public void actionPerformed(ActionEvent event)  {
        parentFrame.dispose();
        new Chess(-1);
    }
}