import java.util.ArrayList;

public class Stroke {
	private ArrayList<Integer> listX;
	private ArrayList<Integer> listY;
	private int[] xVals;
	private int[] yVals;
	private int size, maxUp, maxLeft, maxRight, maxDown;

	/**
    * Constructor the class, initializes all variables
    */
	public Stroke(ArrayList<Integer> x, ArrayList<Integer> y, int maxUp, int maxLeft, int maxRight, int maxDown) {
		this.listX = x;
		this.listY = y;
		this.size = x.size();
		this.setXVals(x);
		this.setYVals(y);
		this.maxUp = maxUp;
		this.maxLeft = maxLeft;
		this.maxRight = maxRight;
		this.maxDown = maxDown;
	}
	
	public int[] getXVals() { return this.xVals; }
	public int[] getYVals() { return this.yVals; }		
	public int getSize() { return this.size; }

	public void setXVals(ArrayList<Integer> x) {
		int[] xValues = new int[this.size];
		for (int p = 0; p < this.size; p++) {
			xValues[p] = x.get(p);
		}
		this.xVals = xValues;
	}
	public void setYVals(ArrayList<Integer> y) { 
		int[] yValues = new int[this.size];
		for (int p = 0; p < this.size; p++) {
			yValues[p] = y.get(p);
		}
		this.yVals = yValues; 
	}
	public ArrayList<Integer> getListX() { return this.listX; }
	public ArrayList<Integer> getListY() { return this.listY; }
	public int getMaxUp() { return this.maxUp; }
	public int getMaxLeft() { return this.maxLeft; }
	public int getMaxRight() { return this.maxRight; }
	public int getMaxDown() { return this.maxDown; }

	public void changeXPixel(int index, int value) {
		xVals[index] = value;
		listX.set(index, value);
	}
	public void changeYPixel(int index, int value) {
		yVals[index] = value;
		listY.set(index, value);
	}
	public void changeMax(int changeInX, int changeInY) {
		maxUp -= changeInY;
		maxDown -= changeInY;
		maxLeft -= changeInX;
		maxRight -= changeInX;
	}
}