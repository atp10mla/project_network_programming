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
	private int currentRound = 3;
	private int globalSticks;
	private Player stickWinner;
	private int maxNbrOfPlayers;
	private boolean gameIsRunning = false;
	private boolean readyToStartNewRound = false;
	private final int totalAmountOfRounds = 3;
	
	public Monitor(int numberOfPlayers) {
		maxNbrOfPlayers = numberOfPlayers;
		for(int i=1; i<5; i++)
			for(int j=2; j<=14; j++)
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

	/**
	 * New round (new cards)
	 */
	boolean firstRound = true;
	public synchronized void startNewRound() {
		for(Player p : party) {
			p.setWantedSticks(-1);
		}
		// shuffle the cards.
		shuffle();
		// set trumf for the round
		trumf = getNextCard();
		
		sendCommando(Protocol.NEW_ROUND);
		sendCommando(Protocol.SET_TRUMF);
		notifyAll();
		
		if(!firstRound) {
			roundStarter = getPlayerWithId(getNextPlayer(roundStarter,party.size()));
		} else {
			firstRound = false;
		}
		
		stickStarter = roundStarter;
		fixWantedSticks();
		int start = roundStarter.getId();
		int stop = (roundStarter.getId()==1?party.size():roundStarter.getId()-1);
		System.out.println("start is " + start);
		System.out.println("stop is " + stop);
		while(start != stop) {
			System.out.println("start is " + start);
			if(getPlayerWithId(start).getWantedSticks() > stickStarter.getWantedSticks())
				stickStarter = getPlayerWithId(start);
			start = getNextPlayer(getPlayerWithId(start), party.size());
		}
		
		commands.get(stickStarter).add(Protocol.YOUR_TURN);
		
		//set roundstart for next round.
		notifyAll();
	}

	private int getNextPlayer(Player p, int size) {
		return p.getId() % size + 1;
	}

	/**
	 * Ask every player for number of sticks..
	 */
	public synchronized void fixWantedSticks() {
		int curr = roundStarter.getId();
		int stop;
		if(curr == 1) {
			stop = party.size();
		} else {
			stop = curr-1;
		}
		
		while(curr != stop) {
			Player temp = getPlayerWithId(curr);
			commands.get(temp).add(Protocol.SET_STICKS);
			notifyAll();
			while(temp.getWantedSticks() == -1) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println(temp.getWantedSticks());
			}
			
			curr = curr % party.size() + 1;
//			System.out.println(curr);
		}
		Player temp = getPlayerWithId(curr);
		commands.get(temp).add(Protocol.SET_STICKS);
		notifyAll();
		while(temp.getWantedSticks() == -1) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Used by startGun to start the game. 
	 */
	public synchronized void waitForStart() {
		long startTime = System.currentTimeMillis();
		while(party.size()<maxNbrOfPlayers && (System.currentTimeMillis()-startTime)<(1000*2*60)) {
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
		}
		sendCommando(Protocol.PLAYED_CARD);
		currentStickCards.add(card);
		notifyAll();
		System.out.println("curr stick cards = " + currentStickCards.size() + " party size = " + party.size());
		if(currentStickCards.size() == party.size()) {
			getStickWinner();
		} else {
			stickStarter = getPlayerWithId(getNextPlayer(stickStarter,party.size()));
			commands.get(stickStarter).add(Protocol.YOUR_TURN);		
		}
		if(globalSticks == currentRound) {
			handleRoundEnd();
		}
		notifyAll();
	}
	public synchronized void getStickWinner() {
		Card firstCardInRound = currentStickCards.get(0);
		Card currentBestCard = firstCardInRound;
		stickWinner = firstCardInRound.getOwner();
		globalSticks++;
		for(Card card : currentStickCards) {
			if(card.moreValuableThan(currentBestCard, trumf, firstCardInRound)) {
				stickWinner = card.getOwner();
				currentBestCard = card;
			}
		}
		currentStickCards.clear();
		sendCommando(Protocol.STICK_WINNER);
		notifyAll();
		System.out.println("Round " + currentRound + " stick " + globalSticks + " was won by player " + stickWinner.getId());
		stickStarter = stickWinner;
		stickWinner.addStick();
		System.out.println("globalSticks = " + globalSticks + " currentRound = " + currentRound);
		if(globalSticks != currentRound) {
			commands.get(stickStarter).add(Protocol.YOUR_TURN);
		}
		notifyAll();
	}
	
	private int direction = 1;
	private synchronized void handleRoundEnd() {
		sendCommando(Protocol.ROUND_SCORE);
		globalSticks = 0;
		System.out.println("Current round has ended: " + currentRound);
		currentRound -= direction;
		if(currentRound == totalAmountOfRounds + 1) {
			System.out.println("Game is over.");
			//			System.exit(0);
			// send ultimate winner
			return;
		}
		if(currentRound == 1) {
			direction = -1;
		}
		System.out.println("Starting new round" + currentRound);
		
		readyToStartNewRound = true;
		notifyAll();
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
			System.out.println("CURRENT ROUNDSTARTER: "+roundStarter);
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
		sendCommando(Protocol.SET_TRUMF);
	}
	public Card getTrumf() {
		return trumf;
	}
	
	/**
	 * Add command NEW_GAME to all players. 
	 */
	public synchronized void startGame() {
		gameIsRunning = true;
		sendCommando(Protocol.NEW_GAME);
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
		return party.get(i-1).getScore();
	}
	public synchronized Card getNextCard() {
		return deck.get(cardPosition++);
	}
	
	private Player currentStickSetter;
	
	// TODO 
	public synchronized void setWantedSticks(int nbrOfSticks, Player p) {
		p.setWantedSticks(nbrOfSticks);
		currentStickSetter = p;
		sendCommando(Protocol.SET_WANTED_STICKS);
		notifyAll();
	}
	
	public Player getCurrentSticker() {
		return currentStickSetter;
	}
	public synchronized void waitForNewRoundReady() {
		while(!readyToStartNewRound) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		readyToStartNewRound = false;
		startNewRound();
	}
}
