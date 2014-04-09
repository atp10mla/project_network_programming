package client;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;

import protocol.Card;

public class Monitor {
	private LinkedList<Integer> commandos = new LinkedList<Integer>();
	private boolean choiceNewCard = true;
	private Card nextCard;
	private HashSet<Card> currentTurn = new HashSet<Card>();
	private int nbrOfPlayers;
	private HashSet<Card> currentHand = new HashSet<Card>();
	private int trumf;
	private int playedSuit;
	
	private int nbrOfSpades;
	private int nbrOfHearts;
	private int nbrOfDiamonds;
	private int nbrOfClubs;
	
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
		choiceNewCard = true; 
		
		notifyAll();
	}



	public synchronized void addNextPlayedCard(Card card) {
		currentTurn.add(card);
		if(currentTurn.size() == nbrOfPlayers) {
			// TODO
			currentTurn.clear();
		} else if(currentTurn.size()==1){
			playedSuit = card.getSuit();
		}
		
	}

	public synchronized void setTrumf(int suit) {
		trumf = suit;
	}


	public synchronized void addCardToHand(Card card) {
		if(currentHand.size() == 0 ) {
			nbrOfSpades = 0;
			nbrOfHearts = 0;
			nbrOfDiamonds = 0;
			nbrOfClubs = 0;
		}
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
		currentHand.clear();		
	}

	public synchronized void addStick(int player) {
		playerStick.set(player-1, playerStick.get(player-1)+1);	
	}

	public void addWantedSticks(int player, int sticks) {
		// TODO Auto-generated method stub
		playerStick.set(player-1, sticks);
		
	}
	
}
