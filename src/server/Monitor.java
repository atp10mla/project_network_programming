package server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import protocol.Card;
import protocol.Protocol;

public class Monitor {
	HashMap<Player, LinkedList<Integer>> commands = new HashMap<Player, LinkedList<Integer>>();
	HashMap<Player, LinkedList<Card>> playedCards = new HashMap<Player, LinkedList<Card>>();
	ArrayList<Card> currentStickCards = new ArrayList<Card>();
	ArrayList<Player> party = new ArrayList<Player>();
	ArrayList<Card> deck = new ArrayList<Card>();
	private Card trumf;
	private Player stickStarter, roundStarter;
	private int cardPosition = 0;
	private int numberOfRounds = 3;
	private int currentRound = numberOfRounds;
	private int globalSticks;
	private Player stickWinner;
	private int maxNbrOfPlayers;
	private boolean gameIsRunning = false;
	private boolean readyToStartNewRound = false;
	private LinkedList<Integer> roundNumbers = new LinkedList<Integer>();

	public Monitor(int numberOfPlayers) {
		maxNbrOfPlayers = numberOfPlayers;
		for (int i = 1; i < 5; i++)
			for (int j = 2; j <= 14; j++)
				deck.add(new Card(i, j));
		shuffle();
		for (int i = currentRound; i >= 1; i--) {
			roundNumbers.add(i);
		}
		for (int i = 1; i <= currentRound; i++) {
			roundNumbers.add(i);
		}
		currentRound = roundNumbers.poll();
	}

	// handle order of players
	public synchronized int getNextCommand(Player p) {
		while (commands.get(p).isEmpty()) {
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
	public synchronized void startNewRound() {
		for (Player p : party) {
			p.setWantedSticks(-1);
		}
		// shuffle the cards.
		shuffle();
		// set trumf for the round
		trumf = getNextCard();

		addCommandForAll(Protocol.NEW_ROUND);
		addCommandForAll(Protocol.SET_TRUMF);
		notifyAll();

		askPlayersForWantedSticks();

		stickStarter = roundStarter;
		roundStarter = getPlayerWithId(getNextPlayer(roundStarter, party.size()));
		int start = roundStarter.getId();
		int stop = (roundStarter.getId() == 1 ? party.size() : roundStarter
				.getId() - 1);
		// System.out.println("start is " + start);
		// System.out.println("stop is " + stop);

		int curr = start;
		while (curr != stop) {
			// System.out.println("current is " + curr);
			if (getPlayerWithId(curr).getWantedSticks() > stickStarter
					.getWantedSticks())
				stickStarter = getPlayerWithId(curr);
			curr = getNextPlayer(getPlayerWithId(curr), party.size());
		}

		commands.get(stickStarter).add(Protocol.YOUR_TURN);

		// set roundstart for next round.
		notifyAll();
	}

	private int getNextPlayer(Player p, int size) {
		return p.getId() % size + 1;
	}

	/**
	 * Ask every player for number of sticks..
	 */
	public synchronized void askPlayersForWantedSticks() {
		int curr = roundStarter.getId();
		int playersAsked = 0;
		while (playersAsked < party.size()) {
			Player temp = getPlayerWithId(curr);
			commands.get(temp).add(Protocol.SET_STICKS);
			notifyAll();
			while (temp.getWantedSticks() == -1) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("WANTED STICKS: " + temp.getWantedSticks());
			}

			curr = curr % party.size() + 1;
			playersAsked++;
		}
	}

	/**
	 * Used by startGun to start the game.
	 */
	public synchronized void waitForStart() {
		long startTime = System.currentTimeMillis();
		while (party.size() < maxNbrOfPlayers
				&& (System.currentTimeMillis() - startTime) < (1000 * 2 * 60)) {
			try {
				wait(1000 * 2 * 60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	// main function, really
	public synchronized void sendPlayedCard(Card card) {
		for (Player p : party) {
			// if(p.equals(card.getOwner()))
			// continue;
			playedCards.get(p).add(card);
		}
		addCommandForAll(Protocol.PLAYED_CARD);
		currentStickCards.add(card);
		notifyAll();
		System.out.println("curr stick cards = " + currentStickCards.size()
				+ " party size = " + party.size());
		if (currentStickCards.size() == party.size()) {
			calculateStickWinner();
		} else {
			stickStarter = getPlayerWithId(getNextPlayer(stickStarter,
					party.size()));
			commands.get(stickStarter).add(Protocol.YOUR_TURN);
		}
		if (globalSticks == currentRound) {
			handleRoundEnd();
		}
		notifyAll();
	}

	public synchronized void calculateStickWinner() {
		Card firstCardInRound = currentStickCards.get(0);
		Card currentBestCard = firstCardInRound;
		stickWinner = firstCardInRound.getOwner();
		globalSticks++;
		for (Card card : currentStickCards) {
			if (card.moreValuableThan(currentBestCard, trumf, firstCardInRound)) {
				stickWinner = card.getOwner();
				currentBestCard = card;
			}
		}
		currentStickCards.clear();
		addCommandForAll(Protocol.STICK_WINNER);
		notifyAll();
		System.out.println("Round " + currentRound + " stick " + globalSticks
				+ " was won by player " + stickWinner.getId());
		stickStarter = stickWinner;
		stickWinner.addStick();
		System.out.println("globalSticks = " + globalSticks
				+ " currentRound = " + currentRound);
		if (globalSticks != currentRound) {
			commands.get(stickStarter).add(Protocol.YOUR_TURN);
		}
		notifyAll();
	}

	private synchronized void handleRoundEnd() {
		for (int i = 0; i < party.size(); i++) {
			party.get(i).calculateScoreForRound();
			party.get(i).clearSticks();
		}
		addCommandForAll(Protocol.ROUND_SCORE);
		globalSticks = 0;
		System.out.println("Current round has ended: " + currentRound);
		if (roundNumbers.isEmpty()) {
			System.out.println("Game is over.");
			addCommandForAll(Protocol.SET_WINNER);
		} else {
			currentRound = roundNumbers.poll();
			System.out.println("Starting new round" + currentRound);
			readyToStartNewRound = true;
			notifyAll();
		}
	}

	private Player getPlayerWithId(int i) {
		Player ret = null;
		for (Player p : party) {
			if (p.getId() == i) {
				ret = p;
			}
		}
		return ret;
	}

	public synchronized void addPlayer(Player p) {
		if (gameIsRunning) {
			return;
		}
		party.add(p);
		playedCards.put(p, new LinkedList<Card>());
		if (party.size() == 1) {
			roundStarter = party.get(0);
			System.out.println("CURRENT ROUNDSTARTER: " + roundStarter);
			stickStarter = party.get(0);
			stickWinner = party.get(0);
		}
		commands.put(p, new LinkedList<Integer>());
		notifyAll();
	}

	public synchronized void removePlayer(Player p) {
		party.remove(p);
	}

	// public synchronized void setTrumf(Card suit) {
	// trumf = suit;
	// addCommandForAll(Protocol.SET_TRUMF);
	// }
	public Card getTrumf() {
		return trumf;
	}

	/**
	 * Add command NEW_GAME to all players.
	 */
	public synchronized void startGame() {
		gameIsRunning = true;
		addCommandForAll(Protocol.NEW_GAME);
		notifyAll();
	}

	public synchronized void addCommandForAll(int com) {
		for (Player p : party) {
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

	public int getNumberOfRounds() {
		return numberOfRounds;
	}

	
	public int getRoundNumber() {
		return currentRound;
	}

	public synchronized Player returnStickWinner() {
		return stickWinner;
	}

	public int getRoundScoreOfPlayer(int i) {
		return party.get(i - 1).getRoundScore();
	}

	public int getTotalScoreOfPlayer(int i) {
		return party.get(i - 1).getTotalScore();
	}

	public synchronized Card getNextCard() {
		return deck.get(cardPosition++);
	}

	private Player currentStickSetter;

	// TODO
	public synchronized void setWantedSticks(int nbrOfSticks, Player p) {
		p.setWantedSticks(nbrOfSticks);
		currentStickSetter = p;
		addCommandForAll(Protocol.SET_WANTED_STICKS);
		notifyAll();
	}

	public Player getCurrentSticker() {
		return currentStickSetter;
	}

	public synchronized void waitForNewRoundReady() {
		while (!readyToStartNewRound) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		readyToStartNewRound = false;
	}
	
	public int getWinner() {
		int id = -1;
		int maxScore = -1;
		for (Player p : party) {
			if (p.getTotalScore() > maxScore) {
				id = p.getId();
				maxScore = p.getTotalScore();
			} else if (p.getTotalScore() == maxScore) {
				id = -1;
			}
		}
		return id;
	}
}
