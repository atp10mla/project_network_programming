package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import protocol.Card;
import protocol.Protocol;

public class Monitor {
	HashMap<Player, LinkedList<Integer> > commands = new HashMap<Player, LinkedList<Integer> >();
	HashMap<Player, LinkedList<Card> > playedCards = new HashMap<Player, LinkedList<Card> >();
	ArrayList<Player> party = new ArrayList<Player>();
	ArrayList<Card> deck = new ArrayList<Card>();
	private int trumf;
	private Player roundStarter, stickStarter;
	private int cardPosition = 0;
	private int currentRound = 10;
	private boolean canStartNewRound = true; 
	private boolean gameIsRunning = false;

	public Monitor() {
		for(int i=1; i<14; i++) {
			for(int j=1; j<5; j++) {
				deck.add(new Card(i,j));
			}
		}
		shuffle();
	}

	// handle who win stick

	// handle order of players

	public synchronized void startGame() {
		gameIsRunning = true;
		notifyAll();
	}

	public synchronized Card getNextCard() {
		return deck.get(cardPosition++);
	}

	public synchronized void sendCommando(int com) {
		try {
			for(Player p : party) {
				p.getOs().write(com);
				p.getOs().flush();
			}
		} catch (IOException e) {
			System.out.println("Could not send from monitor");
			System.exit(1);
		}
	}

	public synchronized void addPlayer(Player p) {
		if(gameIsRunning) {
			return;
		}
		party.add(p);
		if(party.size() == 1) {
			roundStarter = party.get(0);
			stickStarter = party.get(0);
		}
		commands.put(p, new LinkedList<Integer>());
		notifyAll();
	}

	public synchronized void removePlayer(Player p) {
		party.remove(p);
	}

	public synchronized void setTrumf(int suit) {
		trumf = suit;
	}

	public int getTrumf() {
		return trumf;
	}

	public synchronized void setFirstOfRound(int index) {
		//firstOfRound = party.get(index);
	}

	public synchronized int getFirstThisRound() {
		return 0;
	}

	public int getNextCommando(Player p) {
		while(commands.get(p.getId()).isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return commands.get(p.getId()).pop();
	}

	public void executeCommando(int com) {
	}

	public synchronized void shuffle() {
		Collections.shuffle(deck);
		cardPosition = 0;
	}

	public synchronized void readyForNewRound() {
		canStartNewRound = true;
		notifyAll();
	}

	// incorrect
	public synchronized void startNewRound() {
		while(!canStartNewRound) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		shuffle();
		for(Player p : party) {
			try {
				p.getOs().write(Protocol.NEW_ROUND);
				for(int i=1;i<currentRound; i++) {
					p.getOs().write(deck.get(cardPosition).getSuit());
					p.getOs().write(deck.get(cardPosition).getValue());
					cardPosition++;
				}
				p.getOs().flush();
			} catch (IOException e) {
				System.out.println("Player " + p.getName() + " messed up. Throw a shoe on this player.");
				System.exit(1);
			}
		}
		canStartNewRound = false;
		notifyAll();
	}

	public synchronized void waitForStart() {
		long startTime = System.currentTimeMillis();
		while(party.size()<5 && (System.currentTimeMillis()-startTime)<(1000*2*60)) {
			try {
				wait(1000*2*60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		startGame();
	}

	public synchronized void sendPlayedCard(Card card) {
		for(Player p : party) {
			if(p.equals(card.getOwner()))
				continue;
			commands.get(p).add(Protocol.PLAYED_CARD);
			playedCards.get(p).add(card);
		}
		notifyAll();
	}

	public synchronized Card getNextPlayedCard(Player p) {
		return playedCards.get(p).pop();
	}

	public synchronized Player getStickWinner() {
		
		return null;
	}

	public int getNumberOfPlayers() {
		return party.size();
	}

	public int getRoundNumber() {
		return currentRound;
	}
}
