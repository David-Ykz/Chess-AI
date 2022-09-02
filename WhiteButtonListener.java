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