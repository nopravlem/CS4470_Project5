import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.image.BufferedImage;

public class PageContainer extends JComponent implements ActionListener, MouseListener {
    private static final int WIDTH = 500, HEIGHT = 500;
    private static final int CHANGE_RATE = 20;
    private static final int FINAL_VAL = 25;

    private ArrayList<NewPage> pages;
	private ArrayList<ChildSize> finalDestination;
	private ArrayList<IterationValues> intermediate; 

	private int totalPages, currPage, highestNum, gridSize, testTime;
	private double widthIterate, heightIterate;
	private boolean overviewMode, gridMode, addPage, deletePage, zoomIn, zoomOut;
    private String gestures;

    private NewPage userArea;
    private Gesture givenGesture;

	private Timer timer;
	private BufferedImage topPage, bottomPage, leftPage, centerPage, rightPage;

	/**
	* Constructor for the Page Container class, creates the first page in container.
	*/
    public PageContainer() {
		setMaximumSize(new Dimension(WIDTH, HEIGHT));
		setLayout(new BorderLayout());
        setBackground(new Color(193, 243, 255));

		pages = new ArrayList();
		intermediate = new ArrayList();
		finalDestination = new ArrayList();

		userArea = new NewPage(1);
		pages.add(userArea);
		totalPages++;
		highestNum++;
		testTime = 1;

        addMouseListener(userArea);
        addMouseMotionListener(userArea);
        addKeyListener(userArea);

        this.add(userArea, BorderLayout.CENTER);

        topPage = null;
        bottomPage = null;

        gestures = "";
    }

	@Override
    public void paintComponent(Graphics g) {
		Graphics2D graph = (Graphics2D) g;

		if (overviewMode) {
			graph.setColor(Color.BLUE);
			graph.fillRect(0,0,this.getWidth(), this.getHeight());
			graph.setColor(Color.BLACK);
			
			for(int x = 0; x < pages.size(); x++) {
				if (x != currPage) {
					Graphics2D graphCopy = (Graphics2D)graph.create();

					IterationValues iterVs = intermediate.get(x);
					double currXIter = testTime*iterVs.getXIterate();
					double currYIter = testTime*iterVs.getYIterate();
					double currWidthIter = Math.pow(widthIterate, testTime);
					double currHeightIter = Math.pow(heightIterate, testTime);

					graphCopy.translate(currXIter, currYIter);
					graphCopy.scale(currWidthIter, currHeightIter);
					pages.get(x).paint(graphCopy);
				}
			}

			Graphics2D graphCopy = (Graphics2D)graph.create();

			IterationValues iterVs = intermediate.get(currPage);
			double currXIter = testTime*iterVs.getXIterate();
			double currYIter = testTime*iterVs.getYIterate();
			double currWidthIter = Math.pow(widthIterate, testTime);
			double currHeightIter = Math.pow(heightIterate, testTime);

			graphCopy.translate(currXIter, currYIter);
			graphCopy.scale(currWidthIter, currHeightIter);
			pages.get(currPage).paint(graphCopy);
		}
	}

	@Override
	public void paintChildren(Graphics g) {
		Graphics2D graph = (Graphics2D) g;
		if (!overviewMode) {
			super.paintChildren(g);
		}
	}

    /**
    * Redraws page when it needs to turn
    */
    public void redrawPage() {

        //take care of add and delete
        if (deletePage) {
        	removeMouseListener(userArea);
        	removeMouseMotionListener(userArea);
        	removeKeyListener(userArea);
        	this.remove(userArea);
        }
        if (pages.size() > 0) {
        	if (!deletePage) {
	        	removeMouseListener(userArea);
	        	removeMouseMotionListener(userArea);
	        	removeKeyListener(userArea);
        	}
	        userArea = pages.get(currPage);
	        if(addPage)
	        	this.add(userArea);
	    } else {
	    	userArea = new NewPage(1);
			pages.add(userArea);
			this.add(userArea, BorderLayout.CENTER);
			totalPages = 1;
			highestNum = 1;
			currPage = 0;
	    } 
        addPage = false;
        deletePage = false;

        //set current to visible and others to not visible
        showCurrentPage(userArea);

        this.revalidate();
        this.repaint();
    }

    /**
    * Turns the page with animation
    *
    * @param forward - whether or not the page is flipping forward or backward
    */
    public void turnPage(boolean forward) {
        topPage = makeOffscreenImage(userArea);
        userArea = pages.get(currPage);
        bottomPage = makeOffscreenImage(userArea);
        if (totalPages > 1)
            createLeftAndRight(userArea, currPage, totalPages - 1);

        userArea.pageTurn(topPage, bottomPage, forward);
        userArea.setLeftAndRight(leftPage, rightPage);
        redrawPage();
    }

    /**
    * Creating the buffered images when we turn the pages
    * Checks if it is first page or last page or neither.
    * Has different page assignments for each one.
    *
    * @param page - the current NewPage image
    * @param currentP - the current page
    * @param totalP - the total amount of pages
    */
    public void createLeftAndRight(NewPage page, int currentP, int totalP) {
        if (currentP == 0) {
            leftPage = makeOffscreenImage(page);
            page = pages.get(currentP + 1);
            rightPage = makeOffscreenImage(page);
            userArea.setLeftAndRight(leftPage, rightPage);
        } else if (currentP == totalP) {
            rightPage = makeOffscreenImage(page);
            page = pages.get(currentP - 1);
            leftPage = makeOffscreenImage(page);
            userArea.setLeftAndRight(leftPage, rightPage);
        } else {
            page = pages.get(currentP - 1);
            leftPage = makeOffscreenImage(userArea);
            page = pages.get(currentP + 1);
            rightPage = makeOffscreenImage(userArea);
            userArea.setLeftAndRight(leftPage, rightPage);
        }
    }

    /**
    * Creates the image given a panel
    *
    * @param source - the component to create a buffered image of
    *
    * @return BufferedImage - the image that we return
    */
    public BufferedImage makeOffscreenImage (JComponent source) {
        GraphicsConfiguration gfxConfig = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        BufferedImage offscreenImage = gfxConfig.createCompatibleImage(source.getWidth(), source.getHeight());
        Graphics2D offscreenGraphics = (Graphics2D) offscreenImage.getGraphics();
        
        source.paint(offscreenGraphics);
        return offscreenImage;
    }


    /**
    * Adds a new page
    */
    public void addNewPage() {
		totalPages++;
		highestNum++;
		currPage = totalPages - 1;
		addPage = true;

		NewPage temp = new NewPage(highestNum);
		pages.add(temp);
		redrawPage();
    }

    /**
    * Deletes a page
    */
    public void deletePage() {
    	pages.remove(currPage);
    	if (currPage == totalPages - 1) {
    		highestNum--;
    	} 
    	if (currPage > 0) {
    		currPage--;
    	}
    	if (currPage == 0 && totalPages == 1) {
    		highestNum= 0;
    		totalPages = 0;
    	}
    	totalPages--;
    	deletePage = true;
    	redrawPage();

    }

    /**
    * Goes to the next page
    */
    public void nextPage() {
    	if (currPage < totalPages - 1) {
    		currPage++;
    		turnPage(true);
    	}
    }

    /**
    * Goes to the previous page
    */
    public void prevPage() {
    	if (currPage > 0) {
    		currPage--;
    		turnPage(false);
    	}
    }

    /**
    * Shows program in overview mode
    *
    */
	public void overview() {
		overviewMode = true;
		zoomIn = true;
		testTime = 1;
		calculateLocations();
		calculateIterations(finalDestination);
        for (NewPage page: pages) {
        	page.setVisible(true);
        	NewPage temp = new NewPage(pages.size() + 3);
        	showCurrentPage(temp);
        }
		timer = new Timer(CHANGE_RATE, this);
		timer.start();
		repaint();
	}

	/**
	* Tells the visible child of container if user is drawing a rectangle
	*
	* @param rectSelected - boolean if user is drawing a rectangle
	*/
	public void setRectSelected(boolean rectSelected) {
		userArea.setRectSelected(rectSelected);
	}

	/**
	* Tells the visible child of container if user is drawing an oval
	*
	* @param ovalSelected - boolean if user is drawing an oval
	*/
    public void setOvalSelected(boolean ovalSelected) {
    	userArea.setOvalSelected(ovalSelected);
    }

	/**
	* Tells the visible child of container if user is drawing with ink
	*
	* @param inkSelected - boolean if user is drawing with ink
	*/
    public void setInkSelected(boolean inkSelected) {
    	userArea.setInkSelected(inkSelected);
    }

	/**
	* Makes all the other pages except current page invisible
	*
	* @param currentPage - only page that will stay visible
	*/
    public void showCurrentPage(NewPage currentPage) {
        for (NewPage page: pages) {
        	if (!page.equals(currentPage)) {
        		removeKeyListener(page);
        		removeMouseListener(page);
        		removeMouseMotionListener(page);
        		page.setVisible(false);
        	} else {
		        addMouseListener(page);
		        addMouseMotionListener(page);
		        addKeyListener(page);
        		page.setVisible(true);
        	}
        }
    }

    /**
    * Calculates all the final destinations for every page in container
    * Creates a grid-like structure by rounding up to the next square integer value
    *
    */
	public void calculateLocations() {
		int totalPages = pages.size();
		gridSize = 1;
		for(int x = 1; x < 45000; x++) {
			int lowerBound = (int) Math.pow(x-1, 2);
			int upperBound = (int) Math.pow(x, 2);
			if (totalPages > lowerBound && totalPages <= upperBound) {
				gridSize = x;		
				break;
			}
		}

		int seperatorSpace = 10 * (gridSize + 1);
		int leftOverWidth = this.getWidth() - seperatorSpace;
		int leftOverHeight = this.getHeight() - seperatorSpace;
		int pageOverviewWidth = leftOverWidth/gridSize;
		int pageOverviewHeight = leftOverHeight/gridSize;

		for (int y = 10; (y + pageOverviewHeight) < this.getHeight(); y+= pageOverviewHeight + 10) {
			for (int x = 10; (x + pageOverviewWidth) < this.getWidth(); x+= pageOverviewWidth + 10) {
				if (finalDestination.size() < pages.size()) {
					finalDestination.add(new ChildSize(x, y, pageOverviewWidth, pageOverviewHeight));
				}
			}
		}
	}

	/**
	* Calculates what to iterate during animation for every page in container
	*
	* @param dests - all the final destinations of each page
	*/
	public void calculateIterations(ArrayList<ChildSize> dests) {
		for (ChildSize cH: dests) {
			double xIterate = cH.getX()/(FINAL_VAL*1.0);
			double yIterate = cH.getY()/(FINAL_VAL*1.0);

			double deltaW = (cH.getWidth()/(double) this.getWidth());
			double deltaH = (cH.getHeight()/(double) this.getHeight());
			widthIterate = Math.pow(deltaW, (1.0/FINAL_VAL));
			heightIterate = Math.pow(deltaW, (1.0/FINAL_VAL));

			intermediate.add(new IterationValues(0, 0, this.getWidth(), this.getHeight(), xIterate, yIterate));
		}

	}

	/**
	* Finds which page the user clicked on when in grid mode
	* 
	* @param x - x value of click
	* @param y - y value of click
	*
	* @return int - the page user clicked on, if no page, then return -1
	*/
	public int findWhichPage(int x, int y) {
		for (int page = 0; page < finalDestination.size(); page++) {
			double leftX = finalDestination.get(page).getX();
			double upY = finalDestination.get(page).getY();
			double rightX = finalDestination.get(page).getWidth() + leftX;
			double downY = finalDestination.get(page).getHeight() + upY;

			if (x >= leftX && x <= rightX && y >= upY && y <= downY) {
				// System.out.println("You clicked on page " + page);
				return page;
			}
		}
		return -1;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (zoomIn) {
			testTime++;
			if (testTime > FINAL_VAL) {
				gridMode = true;
				timer.stop();
			}
		} else if (zoomOut) {
			testTime--;
			if (testTime < 1) {
				overviewMode = false;
				zoomOut = false;
				gridMode = false;
				showCurrentPage(pages.get(currPage));
				finalDestination = new ArrayList();
				intermediate = new ArrayList();
				timer.stop();
			}
		}
		repaint();
	}

    @Override
    public void mouseClicked(MouseEvent e) {
    	if (gridMode) {
    		int page = findWhichPage(e.getX(), e.getY());
    		if (page != -1)
    			currPage = page;
    		zoomIn = false;
    		zoomOut = true;
    		timer = new Timer(CHANGE_RATE, this);
    		timer.start();
    	}
    }
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}

    public boolean isOverview() {
    	return this.overviewMode;
    }
}