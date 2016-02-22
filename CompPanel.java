import java.io.IOException;
import java.net.URL;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

public class CompPanel extends JPanel {
    public static final int WIDTH = 500, HEIGHT = 500;
    private static final ArrayList<String> NUM_OF_TABS = new ArrayList() {{
            add("Web Browser");
            add("Address Book");
        }};
    private JTabbedPane tabs;
    private JEditorPane jEPane;
    private JTextField urlText;
    private JButton back, go;
    private JLabel urlLabel;
    
    /**
    * This is the override of the default constructor. 
    */
    public CompPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(153, 255, 204));
        // setBackground(new Color(0, 0, 122));
        this.tabs = new JTabbedPane();
        this.createTabs(tabs, NUM_OF_TABS);
    }

    @Override
    public Dimension getPreferredSize() {
      if (super.isPreferredSizeSet()) {
         return super.getPreferredSize();
      }
      return new Dimension(WIDTH, HEIGHT);
   }

    /**
    * Creates the tabs when passed a tabs Object and the number of tabs
    * with names that we want.
    *
    * @param JTabbedPane tabs - the tabs Object that has all the tabs
    * @param ArrayList numOfTabs - a list of strings that will be the names of the 
    * tabs passed in
    */
    public void createTabs(JTabbedPane tabs, ArrayList<String> numOfTabs) {
        for(String tab : numOfTabs) {
            JPanel temp = new JPanel();
            temp.setPreferredSize(new Dimension((int)(WIDTH/1.053), (int)(HEIGHT/1.111)));
            // temp.setBackground(new Color(235, 255, 224));
            tabs.addTab(tab, temp);
        }
        this.add(tabs);

        //this edits all the given tabs
        this.editWebUser(0);
        this.editAddressBook(1);
    }

    /**
    * This method edits the webuser tab. This adds the top bar of a website to the
    * top of the panel. The rest of the panel is JEditorPane, where we can have
    * HTML data.
    * There is also a scroll bar added.
    *
    * @param int index - the index of the tab in the list of tabs
    */
    private void editWebUser(int index) {
        JPanel webBrowser = new JPanel();
        webBrowser.setLayout(new BorderLayout());
        webBrowser.setPreferredSize(new Dimension((int)(WIDTH/1.053), (int)(HEIGHT/1.111)));

        try {
            jEPane = new JEditorPane("http://www.java.com/");
            jEPane.setEditable(false);
        } catch(IOException ioe) {

        }

        //elements on the top
        back = new JButton("<");
        go = new JButton("Go");
        urlLabel = new JLabel("URL:");
        urlText = new JTextField();
        urlText.setPreferredSize(new Dimension(270,20));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new FlowLayout());
        headerPanel.setBackground(new Color(25, 108, 25));
        headerPanel.add(back);
        headerPanel.add(urlLabel);
        headerPanel.add(urlText);
        headerPanel.add(go);

        //scroll
        JScrollPane scroll = new JScrollPane(jEPane);

        Stack allUrls = new Stack();
        String home = "http://www.java.com/";
        try {
            jEPane.setPage(home.trim());
        } catch (Exception exc) {}
        
        allUrls.push(home);
        try {
            back.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            String curr = (String) allUrls.pop();
                            jEPane.setPage(curr.trim());
                        } catch (Exception ex) {

                        }
                    }
                });

            go.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        try {
                            allUrls.push(urlText.getText().trim());
                            jEPane.setPage(urlText.getText().trim());
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            );

            //hyperlink listener
            jEPane.addHyperlinkListener(
                new HyperlinkListener() {
                    public void hyperlinkUpdate(HyperlinkEvent e) {
                        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                            if (e instanceof HTMLFrameHyperlinkEvent) {
                                HTMLFrameHyperlinkEvent evt = (HTMLFrameHyperlinkEvent) e;
                                HTMLDocument doc = (HTMLDocument) jEPane.getDocument();
                                doc.processHTMLFrameHyperlinkEvent(evt);
                            } else {
                                try {
                                    jEPane.setPage(e.getURL());

                                    allUrls.push(e.getURL().toString());
                                    urlText.setText(e.getURL().toString());
                                } catch (Throwable t) {
                                    t.printStackTrace();
                                }
                            }
                        }
                    }
                }
            );

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

        webBrowser.add(scroll);
        tabs.setComponentAt(index,webBrowser);
    }

    /**
    * This method edits the address book. It adds the a JTable to the top of 
    * the panel, and it adds a JTextArea to the bottom.
    * The JTextArea shows the selected data in JTable
    *
    * @param int index - the index of the tab in the list of tabs
    */
    private void editAddressBook(int index) {
        JPanel addressBook = new JPanel();
        addressBook.setLayout(new BorderLayout());

        JTable book = new JTable(10,4);
        book.setRowHeight(25);
        book.setGridColor(Color.BLACK);
        // book.setMinimumSize(new Dimension(200,200));
        createFalseData(book);
        JScrollPane bookScroll = new JScrollPane(book);
        JTextArea bookInfo = new JTextArea();
        JScrollPane infoScroll = new JScrollPane(bookInfo);

        book.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent event) {
                    if(event.getValueIsAdjusting()) {
                        int rowNum = book.getSelectedRow();
                        if (rowNum > 0) {
                            String output = 
                                "CURRENT PERSON" +
                                "\nName: " + book.getValueAt(rowNum,0) +
                                "\nCell Number: " + book.getValueAt(rowNum,1) +
                                "\nE-Mail: " + book.getValueAt(rowNum,2) +
                                "\nCity, State: " + book.getValueAt(rowNum,3);
                            bookInfo.setText(output);
                        }
                    }
                }
            }
        );
        
        addressBook.add(book, BorderLayout.NORTH);
        addressBook.add(bookScroll);
        addressBook.add(infoScroll);

        tabs.setComponentAt(index, addressBook);
    }

    /**
    * Helper method to create false data and puts them in vectors.
    * Information go in a Vector of Vectors, while the titles go in a seperate Vector
    */
    private void createFalseData(JTable table) {
        table.setValueAt("Name",0,0);
        table.setValueAt("Cell Number",0,1);
        table.setValueAt("E-Mail",0,2);
        table.setValueAt("City, State",0,3);

        //person 1
        table.setValueAt("Timon Pumba",1,0);
        table.setValueAt("202-555-0192",1,1);
        table.setValueAt("timonpumba@hotmail.com",1,2);
        table.setValueAt("Washington D.C.",1,3);
        //person 2
        table.setValueAt("Sofie Turner",2,0);
        table.setValueAt("404-555-0192",2,1);
        table.setValueAt("sofieturner@hotmail.com",2,2);
        table.setValueAt("Atlanta, GA",2,3);
        //person 3
        table.setValueAt("Jamshid Petra",3,0);
        table.setValueAt("518-555-0106",3,1);
        table.setValueAt("jamshid.petra@hotmail.com",3,2);
        table.setValueAt("Albany, NY",3,3);
        //person 4
        table.setValueAt("Danica Solveiga",4,0);
        table.setValueAt("512-555-0115",4,1);
        table.setValueAt("danica.solveiga@hotmail.com",4,2);
        table.setValueAt("Austin, TX",4,3);
        //person 5
        table.setValueAt("Wolfgang Ezekiel",5,0);
        table.setValueAt("617-555-0186",5,1);
        table.setValueAt("wolfgang.ezekiel@hotmail.com",5,2);
        table.setValueAt("Boston, MA",5,3);
        //person 6
        table.setValueAt("Lynsey Akilah",6,0);
        table.setValueAt("904-555-0107",6,1);
        table.setValueAt("lynsey.akilah@hotmail.com",6,2);
        table.setValueAt("Jacksonville, FL",6,3);
        //person 7
        table.setValueAt("Osgar Agathe",7,0);
        table.setValueAt("501-555-0132",7,1);
        table.setValueAt("osgar.agathe@hotmail.com",7,2);
        table.setValueAt("Little Rock, AR",7,3);
        //person 8
        table.setValueAt("Sargon Curtis",8,0);
        table.setValueAt("808-555-0180",8,1);
        table.setValueAt("sargon.curtis@hotmail.com",8,2);
        table.setValueAt("Honolulu, HI",8,3);
        //person 9
        table.setValueAt("Ilarion Lochlann",9,0);
        table.setValueAt("334-555-0154",9,1);
        table.setValueAt("ilarion.lochlann@hotmail.com",9,2);
        table.setValueAt("Montgomery, AL",9,3);
    }
}