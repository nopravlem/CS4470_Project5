import java.awt.BorderLayout;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main {
    /**
    * This runs the program
    *
    * @param args: Arguments for incoming strings
    */
    public static void main(String[] args) {
        JFrame jframe = new JFrame("Courier Wannabe");
        jframe.setLayout(new BorderLayout());
        jframe.setMinimumSize(new Dimension(600, 350));

        JSplitPane overallScreen = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        overallScreen.setDividerSize(0);
        jframe.add(overallScreen);
        
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        overallScreen.setTopComponent(split);
        
        CompPanel compP = new CompPanel();
        split.setLeftComponent(compP);
        UserPanel userP = new UserPanel();
        split.setRightComponent(userP);

        JPanel statusBar = new JPanel();
        statusBar.setLayout(new FlowLayout());
        statusBar.setBackground(Color.GRAY);

        JLabel statusLabel = new JLabel("Current Action: ");
        JTextArea statusText = new JTextArea();
        statusText.setPreferredSize(new Dimension(300,20));
        
        statusBar.add(statusLabel);
        statusBar.add(statusText);

        userP.whichButton(statusText);
        userP.redrawPage();

        overallScreen.setResizeWeight(1.0);
        overallScreen.setBottomComponent(statusBar);

        jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jframe.setResizable(true);
        jframe.pack();
        jframe.setVisible(true);
    }
}