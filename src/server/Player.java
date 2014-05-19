package server;

import java.io.OutputStream;

public class Player {
	private OutputStream os;
	private String name;
	private int id;
	private int roundScore;
	private int totalScore;
	private int sticks;
	private int wantedSticks;
	
	public Player(OutputStream os, String name, int id) {
		this.os = os;
		this.name = name;
		this.id = id;
<<<<<<< HEAD
		score = 5;
=======
		roundScore = 0;
		totalScore = 0;
>>>>>>> 81f72a4f886c51ec5ae14af54c5ee54b75e0598c
		wantedSticks = -1;
	}
	
	public int getWantedSticks() {
		return wantedSticks;
	}
	
	public void setWantedSticks(int v) {
		wantedSticks = v;
	}
	
	public void addStick() {
		sticks++;
<<<<<<< HEAD
		if(sticks == wantedSticks) {
			score = wantedSticks+10;
		} else {
			score = 0;
		}
=======
>>>>>>> 81f72a4f886c51ec5ae14af54c5ee54b75e0598c
	}
	
	public int getSticks() {
		return sticks;
	}
	
	public void clearSticks() {
		sticks = 0;
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
	
	public void calculateScoreForRound() {
		System.out.println("Calculating points for player " + id + ": " + sticks + " sticks " + wantedSticks + " wantedSticks.");
		if(sticks == wantedSticks) {
			roundScore = wantedSticks+10;
			if(roundScore == 10) {
				roundScore = 5;
			}
		} else {
			roundScore = 0;
		}
		totalScore += roundScore;
	}
	
	public int getRoundScore() {
		return roundScore;
	}
	
	public int getTotalScore() {
		return totalScore;
	}
}
