package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import protocol.Card;
import protocol.Player;
import protocol.Protocol;

public class Monitor {
	ArrayList<Player> party = new ArrayList<Player>();
	ArrayList<Card> deck = new ArrayList<Card>(52);
	private int trumf;
	private Player firstOfRound;
	private int cardPosition = 0;
	private int currentRound = 10;

	public Monitor() {
		for(int i=1; i<14; i++) {
			for(int j=1; j<5; j++) {
				deck.add(new Card(i,j));
			}
		}
		shuffle();
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
		party.add(p);
		if(party.size() == 1) {
			firstOfRound = party.get(0);
		}
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
		firstOfRound = party.get(index);
	}

	public synchronized int getFirstThisRound() {
		return 0;
	}

	public int getNextCommando() {
		return 0;
	}

	public void executeCommando(int com) {
	}

	public synchronized void shuffle() {
		Collections.shuffle(deck);
		cardPosition = 0;
	}

	public synchronized void startNewRound() {
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
	}
}
