import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.*;
import java.util.*;

public class UserPanel extends JPanel {
    private static final int WIDTH = 500, HEIGHT = 500;

    private JButton newPage, deletePage, pageForward, pageBackward, overview;
    private JRadioButton freeFormInk, rect, oval, text;
    private JPanel wholeArea, allConts, controls, drawControls, statusBar, currPanel;
    private JLabel statusLabel; 
    private JTextArea statusText;

    private PageContainer container;

    /**
    * Constructor the class, initializes all variables
    */
    public UserPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setLayout(new BorderLayout());
        setBackground(new Color(193, 243, 255));
        
        addContainer();
        addControls();
    }

    /**
    * Extends this to the component class.
    */
    public void redrawPage() {
        container.redrawPage();
    }

    /**
    * Adds the container
    */
    public void addContainer() {
        container = new PageContainer();
        addMouseListener(container);
        this.add(container);
    }

    /**
    * Adds all the controls
    */
    public void addControls() {
        wholeArea = new JPanel();
        allConts = new JPanel();
        controls = new JPanel();
        drawControls = new JPanel();
        wholeArea.setLayout(new BorderLayout());
        allConts.setLayout(new BorderLayout());
        controls.setLayout(new FlowLayout());
        drawControls.setLayout(new FlowLayout());
        
        newPage = new JButton("New Page");
        deletePage = new JButton("Delete Page");
        pageForward = new JButton("Forward");
        pageBackward = new JButton("Back");
        overview = new JButton("Overview");

        freeFormInk = new JRadioButton("Use Ink");
        rect = new JRadioButton("Draw Rectangle");
        oval = new JRadioButton("Draw Oval");
        text = new JRadioButton("Write Text");

        controls.add(newPage);
        controls.add(deletePage);
        controls.add(pageBackward);
        controls.add(pageForward);
        controls.add(overview);

        ButtonGroup group = new ButtonGroup();
        group.add(freeFormInk);
        group.add(rect);
        group.add(oval);
        group.add(text);

        drawControls.add(freeFormInk);
        drawControls.add(rect);
        drawControls.add(oval);
        drawControls.add(text);

        allConts.add(controls, BorderLayout.NORTH);
        allConts.add(drawControls, BorderLayout.SOUTH);
        
        wholeArea.add(allConts, BorderLayout.SOUTH);
        this.add(wholeArea, BorderLayout.SOUTH);
    }

    /**
    * All the action listeners and tells user which button they pressed
    *
    * @param jta: the text area for this method to place
    */ 
    public void whichButton(final JTextArea jta) {
        //all the JButtons
        newPage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jta.setText("You have created a new page");
                if (!container.isOverview())
                    container.addNewPage();
            }
        });        

        deletePage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jta.setText("You have deleted a page");
                if (!container.isOverview())
                    container.deletePage();
            }
        });        

        pageForward.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jta.setText("You have turned the page forward");
                if (!container.isOverview())
                    container.nextPage();
            }
        });

        pageBackward.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jta.setText("You have turned the page backward");
                if (!container.isOverview())
                    container.prevPage();
            }
        });

        overview.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jta.setText("You are viewing an overview");
                if (!container.isOverview())
                    container.overview();
            }
        });

        rect.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    jta.setText("You will draw a rectangle");
                    container.setRectSelected(true);
                } else {
                    container.setRectSelected(false);
                }
            }
        });

        oval.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    jta.setText("You will draw an oval");
                    container.setOvalSelected(true);
                } else {
                    container.setOvalSelected(false);
                }
            }
        });

        text.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                    jta.setText("Does not work right now.");
            }
        });

        freeFormInk.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    jta.setText("You are now using free form ink.");
                    container.setInkSelected(true);
                } else {
                    container.setInkSelected(false);
                }
            }
        });

    }
}