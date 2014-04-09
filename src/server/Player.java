package server;

import java.io.OutputStream;

public class Player {
	private OutputStream os;
	private String name;
	private int id;
	private int score;
	
	public Player(OutputStream os, String name, int id) {
		this.os = os;
		this.name = name;
		this.id = id;
		score = 0;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public int getId() {
		return id;
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
