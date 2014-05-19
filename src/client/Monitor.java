package client;

import java.util.LinkedList;

import protocol.Card;
import protocol.Protocol;

public class Monitor {
	private LinkedList<Integer> commands = new LinkedList<Integer>();
	
	private Card chosenCard;
	private int nbrOfSticks;	

	public synchronized int getNextCommand() {
		while(commands.size() == 0) {
			try {
				wait();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
		int cmd = commands.pop();
		notifyAll();
		return cmd;
	}
	

	public synchronized Card getChosenCard() {		
		while(chosenCard == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Card card = chosenCard;
		chosenCard = null;
		notifyAll();
		return card;
	}
	
	public synchronized void setChosenCard(Card card) {
		chosenCard = card;
		System.out.println(card);
		notifyAll();
	}
	
	public synchronized void addSendCardCommand() {
		commands.add(Protocol.SEND_CARD);
		notifyAll();
	}

	public synchronized int getNumberOfSticks() {
		// TODO Auto-generated method stub
		commands.poll();
		return nbrOfSticks;
	}
	
	public synchronized void addNumberOfSticksCommand(int nbrOfSticks) {
		// TODO Auto-generated method stub
		this.nbrOfSticks = nbrOfSticks;
		commands.addLast(Protocol.SET_STICKS);
		System.out.println("in monitor!");
		notifyAll();
	}
	
	


/*
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
	*/

	/*
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
	*/
	/*
	public synchronized void cleanHand() {
		nbrOfSpades = 0;
		nbrOfHearts = 0;
		nbrOfDiamonds = 0;
		nbrOfClubs = 0;
		currentHand.clear();
		
	}
	*/

	/*
	public synchronized void addStick(int player) {
		guiEvents.addLast(Protocol.STICK_WINNER);
		stickWinner.addLast(player);
		//playerStick.set(player-1, playerStick.get(player-1)+1);
		notifyAll();
	}
	*/

	/* DONT NEED
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
*/
	/*
	public synchronized void showCardsOnGUI() {
		// TODO Auto-generated method stub
		guiEvents.addLast(Protocol.NEW_ROUND);
		notifyAll();
	}
	*/
	/*
	public synchronized HashSet<Card> getCurrentHand() {	
		return currentHand;
	}
	*/

	/* DONT NEED
	public synchronized void playerScore(int i, int read) {
		// TODO Auto-generated method stub
		
		// Add score in vector.
	}
	
	public synchronized void updateScore() {
		guiEvents.addLast(Protocol.ROUND_SCORE);
		notifyAll();
	}
	*/
	
	
}
