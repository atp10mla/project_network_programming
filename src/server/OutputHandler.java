package server;

import java.io.IOException;
import java.io.OutputStream;

import protocol.Card;
import protocol.Protocol;

public class OutputHandler extends Thread {
	private OutputStream os;
	private Monitor monitor;
	private Player p;

	public OutputHandler(OutputStream os, Monitor monitor, Player p) {
		this.os = os;
		this.monitor = monitor;
		this.p = p;
	}

	public void run() {
		while(true) {
			//			try {

			int cmd = monitor.getNextCommando(p); 

			switch(cmd) {
			case Protocol.NEW_GAME:
				makeNewGame();
				break;
			case Protocol.NEW_ROUND:
				startNewRound();
				// måste skicka trumf vid ny runda
				break;
			case Protocol.PLAYED_CARD:
				Card c = monitor.getNextPlayedCard(p);
				writeCommandAndData(c.getOwner().getId());
				writeCommandAndData(c.getSuit());
				writeCommandAndData(c.getValue());
				break;
			case Protocol.ROUND_SCORE:

				break;
			case Protocol.SET_TRUMF:
				writeCommandAndData(Protocol.SET_TRUMF);
				writeCommandAndData(monitor.getTrumf().getSuit());
				writeCommandAndData(monitor.getTrumf().getValue());
				break;
			case Protocol.STICK_WINNER:
				writeCommandAndData(Protocol.STICK_WINNER);
				writeCommandAndData(monitor.getStickWinner().getId());
				break;
			case Protocol.YOUR_TURN:
				writeCommandAndData(Protocol.YOUR_TURN);
				break;
			}

		}

	}

	private void writeCommandAndData(int data) {
		try {
			os.write(data);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void makeNewGame() {
		// send players
		writeCommandAndData(p.getId());
		writeCommandAndData(monitor.getNumberOfPlayers());
		startNewRound();
	}

	private void startNewRound() {
		// send cards for new round
		monitor.startNewRound();
		writeCommandAndData(Protocol.NEW_ROUND);
		int currRound = monitor.getRoundNumber();
		writeCommandAndData(currRound);
		for(int i=0; i<currRound; i++) {
			Card newCard = monitor.getNextCard();
			writeCommandAndData(newCard.getSuit());
			writeCommandAndData(newCard.getValue());
		}
	}
}
