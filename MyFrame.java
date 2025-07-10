import java.awt.*;
import javax.swing.*;

public class MyFrame extends JFrame{
    private static int width = 500;
    private static int height = 500;
    MyPanel panel;
    MyFrame(){

        panel = new MyPanel();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.add(panel);
        this.pack();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}
 