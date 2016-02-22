import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.*;
import java.util.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.net.URL;

public class NewPage extends JComponent implements ActionListener, MouseListener, MouseMotionListener, KeyListener {
    private final static int HALF_PAGE_GAP = 24;
    private final static int TURN_RATE = 40;

    private boolean rectSelected, ovalSelected, inkSelected, textSelected;
    private boolean drawRectangle, drawOval, drawLine, drawPostIt;
    
    private int pageNumber;
    private int startX, startY;
    private int drawX, drawY;
    private int currX, currY;
    private int originX, originY, width, height;

    private int maxUp, maxLeft, maxRight, maxDown;

    private int turnLocation;
    private int finishedTurn;
    private int nextGap, beforeGap;

    private ArrayList<Integer> xValues, yValues;
    private ArrayList<Stroke> inkList;
    private ArrayList<Oval> ovals;
    private ArrayList<Rectangle> rects;

    private ArrayList<Rectangle> movingRects;
    private ArrayList<Oval> movingOvals;
    private ArrayList<Stroke> movingPix;

    private BufferedImage topPage, bottomPage, leftPage, rightPage, top, bottom, pageTurner;

    private boolean rightClicked;
    private boolean deleteSection;
    private boolean movedSection;
    private boolean movingComps;
    private boolean pageTurn;
    private boolean forward;
    private boolean navPageTurn;
    private boolean overview;

    //fix this
    private boolean goForward;

    private Gesture givenGesture;

    private String gestures;
    private Timer timer, gestureTimer;

    public NewPage(int pageNumber) {
        setPreferredSize(new Dimension(500, 500));
        gestures = "";

        xValues = new ArrayList();
        yValues = new ArrayList();
        inkList = new ArrayList();

        rects = new ArrayList();
        ovals = new ArrayList();

        movingRects = new ArrayList();
        movingOvals = new ArrayList();
        movingPix = new ArrayList();

        this.pageNumber = pageNumber;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D graph = (Graphics2D) g;
        if (pageTurn) {
            if (forward) {
                top = topPage.getSubimage(0, 0, turnLocation - HALF_PAGE_GAP, this.getHeight());
                if (navPageTurn) {
                    bottom = rightPage.getSubimage((turnLocation + HALF_PAGE_GAP), 0, this.getWidth() - (turnLocation + HALF_PAGE_GAP), this.getHeight());
                } else {
                    bottom = bottomPage.getSubimage((turnLocation + HALF_PAGE_GAP), 0, this.getWidth() - (turnLocation + HALF_PAGE_GAP), this.getHeight());
                }

                graph.drawImage(top, 0, 0, this);
                int dividerWidth = 2*HALF_PAGE_GAP;
                graph.drawImage(pageTurner, turnLocation - HALF_PAGE_GAP, 0, dividerWidth, this.getHeight(), this);
                graph.drawImage(bottom, turnLocation + HALF_PAGE_GAP, 0, this);
            } else {
                top = topPage.getSubimage((turnLocation + HALF_PAGE_GAP), 0, this.getWidth() - (turnLocation + HALF_PAGE_GAP), this.getHeight());
                if (navPageTurn) {
                    bottom = leftPage.getSubimage(0, 0, turnLocation - HALF_PAGE_GAP, this.getHeight());
                } else {
                    bottom = bottomPage.getSubimage(0, 0, turnLocation - HALF_PAGE_GAP, this.getHeight());
                }
                
                graph.drawImage(bottom, 0, 0, this);
                int dividerWidth = 2*HALF_PAGE_GAP;
                graph.drawImage(pageTurner, turnLocation - HALF_PAGE_GAP, 0, dividerWidth, this.getHeight(), this);
                graph.drawImage(top, turnLocation + HALF_PAGE_GAP, 0, this);
            }
        } else {
            graph.setColor(new Color(193, 243, 255));
            graph.fillRect(0,0,this.getWidth(), this.getHeight());

            //grid
            graph.setStroke(new BasicStroke(1));
            for(int width = 0; width < this.getWidth(); width += 10) {
                if (((width - 10) % 40 == 0) && ((width - 10) >= 0)) {
                    graph.setColor(Color.BLUE);
                } else {
                    graph.setColor(new Color(51, 204, 255));
                }
                graph.drawLine(width, 0, width, this.getHeight());
            }
            for(int height = 0; height < this.getHeight(); height += 10) {
                if (((height - 10) % 40 == 0) && ((height - 10) >= 0)) {
                    graph.setColor(Color.BLUE);
                } else {
                    graph.setColor(new Color(51, 204, 255));
                }
                graph.drawLine(0, height, this.getWidth(), height);
            }
            graph.setColor(Color.BLACK);

            //all the lines
            graph.setStroke(new BasicStroke(3));
            for (Stroke stroke: inkList) {
                graph.drawPolyline(stroke.getXVals(), stroke.getYVals(), stroke.getSize());
            }
            //all the rects
            for (Rectangle r: rects) {
                graph.drawRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
            }
            //all the ovals
            for (Oval o: ovals) {
                graph.drawOval(o.getX(), o.getY(), o.getWidth(), o.getHeight());
            }


            graph.setColor(Color.RED);
            //all the moving lines
            for (Stroke stroke: movingPix) {
                graph.drawPolyline(stroke.getXVals(), stroke.getYVals(), stroke.getSize());
            }
            //all the moving rects
            for (Rectangle r: movingRects) {
                graph.drawRect((int)r.getX(), (int)r.getY(), (int)r.getWidth(), (int)r.getHeight());
            }
            //all the moving ovals
            for (Oval o: movingOvals) {
                graph.drawOval(o.getX(), o.getY(), o.getWidth(), o.getHeight());
            }
            graph.setColor(Color.BLACK);

            //current gesture
            int[] xVals = new int[xValues.size()];
            int[] yVals = new int[yValues.size()];
            for (int i = 0; i < xValues.size(); i++) {
                xVals[i] = xValues.get(i);
                yVals[i] = yValues.get(i);
            }
            if (rightClicked)
                graph.setColor(Color.RED);
            graph.drawPolyline(xVals, yVals, xValues.size());
            graph.setColor(Color.BLACK);

            //finished gesture; adding only if left
            if(drawLine) {
                if(!rightClicked) {
                    Stroke tempStroke = new Stroke(xValues, yValues, maxUp, maxLeft, maxRight, maxDown);
                    inkList.add(tempStroke);
                }

                if(rightClicked && (deleteSection || movedSection)) {
                    if (deleteSection) {
                        System.out.println("DELETED");
                        for (int x = 0; x < rects.size(); x++) {
                            Rectangle r = rects.get(x);
                            if (((int)r.getX() > maxLeft) && 
                                ((int)r.getX() < maxRight) && 
                                ((int)r.getX() + (int)r.getWidth() < maxRight) && 
                                ((int)r.getY() > maxUp) && 
                                ((int)r.getY() < maxDown) && 
                                ((int)r.getY() + (int)r.getHeight() < maxDown)) {
                                
                                rects.remove(x);
                                x--;
                            }
                        }
                        for (int x = 0; x < ovals.size(); x++) {
                            Oval o = ovals.get(x);
                            if ((o.getX() > maxLeft) && 
                                (o.getX() < maxRight) && 
                                (o.getX() + o.getWidth() < maxRight) && 
                                (o.getY() > maxUp) && 
                                (o.getY() < maxDown) && 
                                (o.getY() + o.getHeight() < maxDown)) {
                                
                                ovals.remove(x);
                                x--;
                            }
                        }
                        for (int x = 0; x < inkList.size(); x++) {
                            Stroke s = inkList.get(x);
                            if ((s.getMaxLeft() > maxLeft) && 
                                (s.getMaxRight() < maxRight) && 
                                (s.getMaxUp() > maxUp) && 
                                (s.getMaxDown() < maxDown)) { 

                                inkList.remove(x);
                                x--;
                            }
                        }
                    } else if (movedSection) {
                        System.out.println("MOVED");
                        for (int x = 0; x < rects.size(); x++) {
                            Rectangle r = rects.get(x);
                            if (((int)r.getX() > maxLeft) && 
                                ((int)r.getX() < maxRight) && 
                                ((int)r.getX() + (int)r.getWidth() < maxRight) && 
                                ((int)r.getY() > maxUp) && 
                                ((int)r.getY() < maxDown) && 
                                ((int)r.getY() + (int)r.getHeight() < maxDown)) {
                                
                                movingRects.add(rects.get(x));
                                rects.remove(x);
                                x--;
                            }
                        }
                        for (int x = 0; x < ovals.size(); x++) {
                            Oval o = ovals.get(x);
                            if ((o.getX() > maxLeft) && 
                                (o.getX() < maxRight) && 
                                (o.getX() + o.getWidth() < maxRight) && 
                                (o.getY() > maxUp) && 
                                (o.getY() < maxDown) && 
                                (o.getY() + o.getHeight() < maxDown)) {
                                
                                movingOvals.add(ovals.get(x));
                                ovals.remove(x);
                                x--;
                            }
                        }
                        for (int x = 0; x < inkList.size(); x++) {
                            Stroke s = inkList.get(x);
                            if ((s.getMaxLeft() > maxLeft) && 
                                (s.getMaxRight() < maxRight) && 
                                (s.getMaxUp() > maxUp) && 
                                (s.getMaxDown() < maxDown)) { 

                                movingPix.add(inkList.get(x));
                                inkList.remove(x);
                                x--;
                            }
                        }
                        if (movingOvals.size() != 0 || movingRects.size() != 0 || movingPix.size() != 0) {
                            inkSelected = false;
                            movingComps = true;
                        }
                    }
                }

                xValues = new ArrayList<>();
                yValues = new ArrayList<>();
                maxLeft = 0;
                maxDown = 0;
                maxRight = 0;
                maxUp = 0;
                gestures = "";
                drawLine = false;
            }

            //current rectangle
            if (rectSelected) {
                graph.drawRect(originX, originY, width, height);
            }
            //finished rectangle & adding it
            if (drawRectangle) {
                rects.add(new Rectangle(originX, originY, width, height));
                drawRectangle = false;
            }

            //current oval
            if (ovalSelected) {
                graph.drawOval(originX, originY, width, height);
            }
            //finished oval & adding it
            if (drawOval) {
                ovals.add(new Oval(originX, originY, width, height));
                drawOval = false;
            }

            //write the page number
            graph.setFont(new Font("TimesRoman", Font.PLAIN, 20)); 
            graph.drawString("" + (pageNumber), this.getWidth() - 30, this.getHeight() - 10);
        }
    }
    //mouse listener
    @Override
    public void mouseClicked(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {
        startX = e.getX();
        startY = e.getY();

        if (inkSelected || movingComps) {
            drawX = startX;
            drawY = startY;
        }

        if (inkSelected) {
            maxUp = startY;
            maxDown = startY;
            maxLeft = startX;
            maxRight = startX;
            if (SwingUtilities.isLeftMouseButton(e))  {
                rightClicked = false;
            } else if (SwingUtilities.isRightMouseButton(e)) {
                rightClicked = true;
            }
            
            if (rightClicked){
                if ((startX > this.getWidth() - (2*HALF_PAGE_GAP + 1)) || (startX < (2*HALF_PAGE_GAP + 1))) {
                    pageTurn = true;
                    navPageTurn = true;
                }
            }
        }
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        if(rectSelected) {
            drawRectangle = true;
            repaint();
        }
        if(ovalSelected) {
            drawOval = true;
            repaint();
        }
        if(inkSelected) {
            drawLine = true;
            if (rightClicked) {
                givenGesture = new Gesture(gestures, false);
                if (givenGesture.getRightArrow()) {
                    goForward = true;
                } else if (givenGesture.getLeftArrow()) {
                    goForward = false;
                }
                deleteSection = givenGesture.getDeleteSection();
                movedSection = givenGesture.getMovedSection();
            }
            repaint();
        }

        if (navPageTurn) {
            int releasePlace = e.getX();
            if (releasePlace >= this.getWidth()/2) {
                finishedTurn = this.getWidth() - 1;
                forward = false;
            } else {
                finishedTurn = 1;
                forward = true;
            }

            gestureTimer = new Timer(TURN_RATE, this);
            gestureTimer.start();
            repaint();
        }

        if(movingComps) {
            for (Rectangle r: movingRects) {
                rects.add(r);
            }
            for (Oval o: movingOvals) {
                ovals.add(o);
            }
            for (Stroke s: movingPix) {
                inkList.add(s);
            }
            inkSelected = true;
            movingComps = false;
            movingRects = new ArrayList();
            movingOvals = new ArrayList();
            movingPix = new ArrayList();
            repaint();
        }
    }

    //mouse motion listener
    @Override
    public void mouseDragged(MouseEvent e) {

        if(rectSelected || ovalSelected) {
            if(startX > e.getX()) {
                originX = e.getX();
                width = startX - e.getX();
            } else {
                originX = startX;
                width = e.getX() - startX;
            }

            if(startY > e.getY()) {
                originY = e.getY();
                height = startY - e.getY();
            } else {
                originY = startY;
                height = e.getY() - startY;
            }
            repaint();
        }

        if(inkSelected) {
            currX = e.getX();
            currY = e.getY();
            if(rightClicked) {
                int changeInX = drawX - currX;
                int changeInY = drawY - currY;
                if ((changeInX == 0) && (changeInY > 0)) { gestures += "N"; }
                if ((changeInX == 0) && (changeInY < 0)) { gestures += "S"; }
                if ((changeInX < 0) && (changeInY == 0)) { gestures += "E"; }
                if ((changeInX > 0) && (changeInY == 0)) { gestures += "W"; }

                if ((changeInX < 0) && (changeInY > 0)) { gestures += "A"; }
                if ((changeInX < 0) && (changeInY < 0)) { gestures += "B"; }
                if ((changeInX > 0) && (changeInY < 0)) { gestures += "C"; }
                if ((changeInX > 0) && (changeInY > 0)) { gestures += "D"; }
            }

            if (currX < maxLeft) { maxLeft = currX; }
            if (currX > maxRight) { maxRight = currX; }
            if (currY > maxDown) { maxDown = currY; }
            if (currY < maxUp) { maxUp = currY; }
            drawX = currX;
            drawY = currY;

            //TODO: change drawX & Y -> prevX?
            xValues.add(drawX);
            yValues.add(drawY);
            repaint();
        }
        if (navPageTurn) {
            if (currX > (this.getWidth() - (HALF_PAGE_GAP + 1))) {
                turnLocation = this.getWidth() - (HALF_PAGE_GAP + 1);
            } else if(currX < (HALF_PAGE_GAP + 1)) {
                turnLocation = (HALF_PAGE_GAP + 1);
            } else {
                turnLocation = currX;
            }
            repaint();
        }

        if (movingComps) {
            currX = e.getX();
            currY = e.getY();
            int changeInX = drawX - currX;
            int changeInY = drawY - currY;
            for (Rectangle r: movingRects) {
                int x = (int)r.getX() - changeInX;
                int y = (int)r.getY() - changeInY;
                r.setLocation(x, y);
            }
            for (Oval o: movingOvals) {
                int x = o.getX() - changeInX;
                int y = o.getY() - changeInY;
                o.setX(x);
                o.setY(y);
            }
            for (Stroke s: movingPix) {
                for (int pixel = 0; pixel < s.getSize(); pixel++) {
                    int x = s.getListX().get(pixel) - changeInX;
                    int y = s.getListY().get(pixel) - changeInY;
                    
                    s.changeXPixel(pixel, x);
                    s.changeYPixel(pixel, y);
                }
                s.changeMax(changeInX, changeInY);
            }
            drawX = currX;
            drawY = currY;
            repaint();
        }
    }
    @Override
    public void mouseMoved(MouseEvent e) {}

    //key listener
    @Override
    public void keyPressed(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}

    /**
    * Tells if user is drawing with ink
    *
    * @param inkSelected - boolean if user is drawing with ink or not
    */
    public void setInkSelected(boolean inkSelected) {
        this.inkSelected = inkSelected;
    }

    /**
    * Tells if user is drawing a rectangle
    *
    * @param rectSelected - boolean if user is drawing a rectangle or not
    */
    public void setRectSelected(boolean rectSelected) {
        this.rectSelected = rectSelected;
    }

    /**
    * Tells if user is drawing an oval
    *
    * @param ovalSelected - boolean if user is drawing an oval or not
    */
    public void setOvalSelected(boolean ovalSelected) {
        this.ovalSelected = ovalSelected;
    }

    /**
    * Sets the images of left and right for animation
    *
    * @param leftPage - the left page image
    * @param rightPage - the right page image
    */
    public void setLeftAndRight(BufferedImage leftPage, BufferedImage rightPage) {
        this.leftPage = leftPage;
        this.rightPage = rightPage;
    }

    /**
    * This does the action of turning the page, and says if it is going forward or backward
    *
    * @param topPage - which page is on top
    * @param bottomPage - which page is on bottom
    * @param forward - boolean if the page is turning forward or not
    */
    public void pageTurn(BufferedImage topPage, BufferedImage bottomPage, boolean forward) {
        this.topPage = topPage;
        this.bottomPage = bottomPage;
        this.forward = forward;

        if (forward) {
            this.setNextPageTurn();
        } else {
            this.setPrevPageTurn();
        }
        pageTurn = true;

        timer = new Timer(TURN_RATE, this);
        timer.start();
    }

    public void setNextPageTurn() {
        try {
            pageTurner = ImageIO.read(new File("PageTurnerNext.png"));
        } catch (IOException e) {}
        turnLocation = this.getWidth() - (HALF_PAGE_GAP + 1);
        finishedTurn = 1;
    }

    public void setPrevPageTurn() {
        try {
            pageTurner = ImageIO.read(new File("PageTurnerPrev.png"));
        } catch (IOException e) {}
        turnLocation = 1 + HALF_PAGE_GAP;
        finishedTurn = this.getWidth() - 1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (forward) {
            turnLocation -= 24;
            if (turnLocation - HALF_PAGE_GAP < finishedTurn) {
                navPageTurn = false;
                pageTurn = false;
                timer.stop();
                if (navPageTurn)
                    gestureTimer.stop();
            }
        } else {
            turnLocation += 24;
            if (turnLocation + HALF_PAGE_GAP > finishedTurn) {
                navPageTurn = false;
                pageTurn = false;
                timer.stop();
                if (navPageTurn)
                    gestureTimer.stop();
            }
        }
        repaint();
    }
    
    public Gesture getGesture() {
        return this.givenGesture;
    }
    public void setPageNum(int pageNumber) {
        this.pageNumber = pageNumber;
    }
    public boolean getTurnPage() {
        return this.goForward;
    }

    //debugging
    public int getPageNum() {
        return this.pageNumber;
    }
    public int getNumOfStrokes() {
        return inkList.size();
    }
}