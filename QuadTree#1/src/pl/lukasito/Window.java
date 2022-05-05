package pl.lukasito;

import javax.swing.*;
import java.awt.*;

public class Window {
    protected JFrame frame;
    AlgAndPanel panel;

    Window(){
        frame = new JFrame("QuadTree Visualization");

        panel = new AlgAndPanel(frame.getContentPane());
        //frame.add(panel);
        frame.setSize(1000,1000);
        //frame.pack();
        //panel.setPreferredSize(new Dimension(1000,1000));
        //frame.add(panel);
        //frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
        frame.setVisible(true);
    }
}
