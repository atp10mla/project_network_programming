package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import protocol.Card;
import protocol.Protocol;

public class Monitor {
	HashMap<Player, LinkedList<Integer> > commands = new HashMap<Player, LinkedList<Integer> >();
	HashMap<Player, LinkedList<Card> > playedCards = new HashMap<Player, LinkedList<Card> >();
	ArrayList<Card> currentStickCards = new ArrayList<Card>();
	ArrayList<Player> party = new ArrayList<Player>();
	ArrayList<Card> deck = new ArrayList<Card>();
	private Card trumf;
	private Player stickStarter, roundStarter;
	private int cardPosition = 0;
	private int currentRound = 10;
	private int globalSticks;
	private Player stickWinner;
	private boolean gameIsRunning = false;

	public Monitor() {
		for(int i=1; i<5; i++)
			for(int j=1; j<14; j++)
				deck.add(new Card(i,j));
		shuffle();
	}

	// handle order of players
	public synchronized int getNextCommando(Player p) {
		while(commands.get(p).isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return commands.get(p).pop();
	}

	public synchronized void startNewRound() {
		shuffle();
		trumf = getNextCard();
		for(Player p : party) {
			commands.get(p).add(Protocol.NEW_ROUND);
			commands.get(p).add(Protocol.SET_TRUMF);
		}
		// fix wanted sticks
		commands.get(roundStarter).add(Protocol.YOUR_TURN);
		stickStarter = stickWinner;
		roundStarter = getPlayerWithId((roundStarter.getId()+1) % (party.size() + 1));
		notifyAll();
	}

	public synchronized void waitForStart() {
		long startTime = System.currentTimeMillis();
		while(party.size()<2 && (System.currentTimeMillis()-startTime)<(1000*2*60)) {
			try {
				wait(1000*2*60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		startGame();
		startNewRound();
	}

	// main function, really
	public synchronized void sendPlayedCard(Card card) {
		for(Player p : party) {
			//if(p.equals(card.getOwner()))
			//	continue;
			playedCards.get(p).add(card);
			commands.get(p).add(Protocol.PLAYED_CARD);
		}
		currentStickCards.add(card);
		if(currentStickCards.size() == party.size()) {
			globalSticks++;
			getStickWinner();
			// fix wanted sticks stuff
			commands.get(stickStarter).add(Protocol.YOUR_TURN);
		} else {
			stickStarter = getPlayerWithId((stickStarter.getId()) % (party.size()) + 1);
			commands.get(stickStarter).add(Protocol.YOUR_TURN);		
		}
//		stickWinner.addStick();
		if(globalSticks == currentRound) {
			handleRoundEnd();
		}
		notifyAll();
	}

	private synchronized void handleRoundEnd() {
		for(Player p : party) {
			commands.get(p).add(Protocol.ROUND_SCORE);
		}
		globalSticks = 0;

		if(currentRound == 1) {
			System.out.println("Game is over.");
			System.exit(0);
		}
		currentRound--;
		startNewRound();
	}

	public synchronized void getStickWinner() {
		Card firstCardInRound = currentStickCards.get(0);
		Card currentBestCard = firstCardInRound;
		stickWinner = firstCardInRound.getOwner();
		for(Card card : currentStickCards) {
			if(card.moreValuableThan(currentBestCard, trumf, firstCardInRound)) {
				stickWinner = card.getOwner();
				currentBestCard = card;
			}
		}
		currentStickCards.clear();
		for(Player p : party) {
			commands.get(p).add(Protocol.STICK_WINNER);
		}
		System.out.println("Round " + currentRound + " stick " + globalSticks + " was won by player " + stickWinner.getId());
		stickStarter = stickWinner;
		stickWinner.addStick();
	}

	private Player getPlayerWithId(int i) {
		Player ret = null;
		for(Player p : party) {
			if(p.getId() == i) {
				ret = p;
			}
		}
		return ret;
	}
	public synchronized void addPlayer(Player p) {
		if(gameIsRunning) {
			return;
		}
		party.add(p);
		playedCards.put(p, new LinkedList<Card>());
		if(party.size() == 1) {
			roundStarter = party.get(0);
			stickStarter = party.get(0);
			stickWinner = party.get(0);
		}
		commands.put(p, new LinkedList<Integer>());
		notifyAll();
	}
	public synchronized void removePlayer(Player p) {
		party.remove(p);
	}
	public synchronized void setTrumf(Card suit) {
		trumf = suit;
		for(Player p : party) {
			commands.get(p).add(Protocol.SET_TRUMF);
		}
	}
	public Card getTrumf() {
		return trumf;
	}
	public synchronized void startGame() {
		gameIsRunning = true;
		for(Player p : party) {
			commands.get(p).add(Protocol.NEW_GAME);
		}
		notifyAll();
	}
	public synchronized void sendCommando(int com) {
		for(Player p : party) {
			commands.get(p).add(com);
		}
	}
	public synchronized void shuffle() {
		Collections.shuffle(deck);
		cardPosition = 0;
	}
	public synchronized void readyForNewRound() {
		//canStartNewRound = true;
		notifyAll();
	}
	public synchronized Card getNextPlayedCard(Player p) {
		return playedCards.get(p).pop();
	}
	public int getNumberOfPlayers() {
		return party.size();
	}
	public int getRoundNumber() {
		return currentRound;
	}
	public synchronized Player returnStickWinner() {
		return stickWinner;
	}
	public int getScoreOfPlayer(int i) {
		return party.get(i).getScore();
	}
	public synchronized Card getNextCard() {
		return deck.get(cardPosition++);
	}
	private Player currentStickSetter;
	public synchronized void setWantedSticks(int nbrOfSticks, Player p) {
		p.setWantedSticks(nbrOfSticks);
		currentStickSetter = p;
		for(Player player : party) {
			commands.get(player).add(Protocol.SET_WANTED_STICKS);
		}
	}
	public Player getCurrentSticker() {
		return currentStickSetter;
	}
}
