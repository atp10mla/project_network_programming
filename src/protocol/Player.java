package protocol;

import java.io.OutputStream;

public class Player {
	private OutputStream os;
	private String name;
	private int score;
	
	public Player(OutputStream os, String name) {
		this.os = os;
		this.name = name;
		score = 0;
	}
	
	public OutputStream getOs() {
		return os;
	}
	
	public String getName() {
		return name;
	}
	
	public void givePoints(int earnedPoints) {
		score += earnedPoints;
	}
	
	public int getScore() {
		return score;
	}
}
