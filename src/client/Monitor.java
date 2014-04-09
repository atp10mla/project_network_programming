package client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import protocol.Card;
import protocol.Protocol;

public class Monitor {
	private LinkedList<Integer> commandos = new LinkedList<Integer>();
	private LinkedList<Integer> guiEvents = new LinkedList<Integer>();
	private LinkedList<Integer> stickWinner = new LinkedList<Integer>();
	private LinkedList<Integer> wantedStick = new LinkedList<Integer>();
	
	private Card nextCard;
	private HashSet<Card> currentTurn = new HashSet<Card>();
	private int nbrOfPlayers;
	private HashSet<Card> currentHand = new HashSet<Card>();
	
	private Card trumf;
	private int playedSuit;
	
	private int nbrOfSpades;
	private int nbrOfHearts;
	private int nbrOfDiamonds;
	private int nbrOfClubs;
	
	// must initialize
	ArrayList<Integer> playerStick;
	ArrayList<Integer> playerWantedSticks;
	
	public synchronized int getNextCommando() {
		while(commandos.size() != 0) {
			try {
				wait();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
		int cmd = commandos.pop();
		notifyAll();
		return cmd;
	}
	
	public synchronized int getNextGUIEvent() {
		while(guiEvents.size() != 0) {
			try {
				wait();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
		int cmd = commandos.pop();
		notifyAll();
		return cmd;
	}
	
	

	public synchronized Card getNextCard() {		
		while(nextCard == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Card card = nextCard;
		nextCard = null;
		notifyAll();
		return card;
	}
	
	public synchronized void choiceNextCard() {		
		guiEvents.addLast(Protocol.YOUR_TURN);
		notifyAll();
	}



	public synchronized void addNextPlayedCard(Card card, int player) {
		currentTurn.add(card);
		// Update gui with card for player... with boolean...
		if(currentTurn.size() == nbrOfPlayers) {
			// TODO
			currentTurn.clear();
		} else if(currentTurn.size()==1){
			playedSuit = card.getSuit();
		}
		
	}

	public synchronized void setTrumf(Card card) {
		trumf = card;
	}


	public synchronized void addCardToHand(Card card) {
		
		switch(card.getSuit()) {
			case Card.CLUBS:
				nbrOfClubs++;
				break;
			case Card.HEARTS:
				nbrOfHearts++;
				break;
			case Card.DIAMONDS:
				nbrOfDiamonds++;
				break;
			case Card.SPADES:
				nbrOfSpades++;
				break;
		}
		currentHand.add(card);
	}

	public synchronized void cleanHand() {
		nbrOfSpades = 0;
		nbrOfHearts = 0;
		nbrOfDiamonds = 0;
		nbrOfClubs = 0;
		currentHand.clear();
		
	}

	public synchronized void addStick(int player) {
		guiEvents.addLast(Protocol.STICK_WINNER);
		stickWinner.addLast(player);
		//playerStick.set(player-1, playerStick.get(player-1)+1);
		notifyAll();
	}

	public void addWantedSticks(int player, int sticks) {
		// TODO Auto-generated method stub
		guiEvents.addLast(Protocol.SET_WANTED_STICKS);
		wantedStick.addLast(player);
		playerStick.set(player-1, sticks);
		notifyAll();
	}
	
	public int addStick() {
		return stickWinner.pop();
	}

	public synchronized void showCardsOnGUI() {
		// TODO Auto-generated method stub
		guiEvents.addLast(Protocol.NEW_ROUND);
		notifyAll();
	}
	public synchronized HashSet<Card> getCurrentHand() {	
		return currentHand;
	}

	public synchronized void playerScore(int i, int read) {
		// TODO Auto-generated method stub
		
		// Add score in vector.
	}
	public synchronized void updateScore() {
		guiEvents.addLast(Protocol.ROUND_SCORE);
		notifyAll();
	}
	
	
}
