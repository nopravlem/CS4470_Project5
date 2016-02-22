import java.util.*;
import java.lang.StringBuilder;
import java.util.regex.Pattern;

public class Gesture {
	private final static String RIGHT_ARROW = "[BES]*B[BES]*[CWS]*C[CWS]*$";
	private final static String LEFT_ARROW = "[CWS]*C[CWS]*[BES]*B[BES]*$";
	private final static String LOWERCASE_PHI = "[CWS]*C[CWS]*[BES]*B[BES]*[AEN]*A[AEN]*[DNW]*D[DNW]*[SBC][SBC][SBC]+$";
	private final static String MOVEMENT_LOOP = "[CWS]*C[CWS]*[BES]*B[BES]*[AEN]*A[AEN]*[DNW]*D[DNW]*$";
	private final static String PAGE_FLIP_NEXT = "";
	private final static String PAGE_FLIP_PREV = "";

	private Pattern right, left, delete, move;

	private String rawGesture;
	private String estimatedGesture;

	private boolean rightArrow;
	private boolean leftArrow;
	private boolean deleteSection;
	private boolean movedSection;
	private boolean pageTurn;

	/**
    * Constructor the class, initializes all variables
    */
	public Gesture(String gestures, boolean pageTurn) {

		right = Pattern.compile(RIGHT_ARROW);
		left = Pattern.compile(LEFT_ARROW);
		delete = Pattern.compile(LOWERCASE_PHI);
		move = Pattern.compile(MOVEMENT_LOOP);

		this.rawGesture = gestures;
		this.pageTurn = pageTurn;
		createEstimatedGesture();
		findMatchingGesture();
	}

	/**
    * Shortens the the directional movement of gesture
    *
    */
	public void createEstimatedGesture() {
		char[] estimate = rawGesture.toCharArray();
		ArrayList<Character> est = new ArrayList();
		for (char e: estimate) {
			est.add(e);
		}
		for (int x = 1; x < est.size(); x++) {
			if (est.get(x).equals(est.get(x - 1))) {
				est.remove(x);
				x--;
			}
		}
		StringBuilder sb = new StringBuilder(est.size());
		for (Character c: est) {
			sb.append(c);
		}
		estimatedGesture = sb.toString();
	}

	/**
	* Finds the matching gestures
	*
	*/
	public void findMatchingGesture() {	
		if (right.matcher(estimatedGesture).find()) {
			rightArrow = true;
		} else if (delete.matcher(estimatedGesture).find()) {
			deleteSection = true;
		} else if (move.matcher(estimatedGesture).find()) {
			movedSection = true;
		} else if (left.matcher(estimatedGesture).find()) {
			leftArrow = true;
		}
	}

	public boolean getRightArrow() {
		return this.rightArrow;
	}

	public boolean getLeftArrow() {
		return this.leftArrow;
	}

	public boolean getDeleteSection() {
		return this.deleteSection;
	}

	public boolean getMovedSection() {
		return this.movedSection;
	}
}