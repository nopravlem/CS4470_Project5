public class IterationValues {
	private int x,y,width,height;
	private double xIterate, yIterate;


	public IterationValues(int x, int y, int width, int height, double xIterate, double yIterate) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.xIterate = xIterate;
		this.yIterate = yIterate;
	}

	public int getX() { return this.x; }
	public int getY() { return this.y; }
	public int getWidth() { return this.width; }
	public int getHeight() { return this.height; }
	public double getXIterate() { return this.xIterate; }
	public double getYIterate() { return this.yIterate; }
	public void setXIterate(double xIterate) { this.xIterate = xIterate; }
	public void setYIterate(double yIterate) { this.yIterate = yIterate; }
}