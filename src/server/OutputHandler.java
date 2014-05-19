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
		monitor.addPlayer(p);
		while(true) {
			int cmd = monitor.getNextCommand(p); 
			switch(cmd) {
			case Protocol.NEW_GAME:
				makeNewGame();
				break;
			case Protocol.NEW_ROUND:
				startNewRound();
				break;
			case Protocol.PLAYED_CARD:
				Card c = monitor.getNextPlayedCard(p);
				System.out.println("Send PLAYED_CARD to player " + p.getId());
				writeCommandAndData(Protocol.PLAYED_CARD);
				writeCommandAndData(c.getOwner().getId());
				writeCommandAndData(c.getSuit());
				writeCommandAndData(c.getValue());
				break;
			case Protocol.ROUND_SCORE:
				writeCommandAndData(Protocol.ROUND_SCORE);
//				System.out.println("Send ROUND_SCORE to player " + p.getId());
				int iter;
				writeCommandAndData((iter = monitor.getNumberOfPlayers()));
				for(int id=1; id<=iter; id++) {
					writeCommandAndData(monitor.getRoundScoreOfPlayer(id));
					writeCommandAndData(monitor.getTotalScoreOfPlayer(id));
				}
				break;
			case Protocol.SET_TRUMF:
				writeCommandAndData(Protocol.SET_TRUMF);
//				System.out.println("Send SET_TRUMF to player " + p.getId());
				writeCommandAndData(monitor.getTrumf().getSuit());
				writeCommandAndData(monitor.getTrumf().getValue());
				break;
			case Protocol.STICK_WINNER:
				writeCommandAndData(Protocol.STICK_WINNER);
//				System.out.println("Send STICK_WINNER to player " + p.getId());
				writeCommandAndData(monitor.returnStickWinner().getId());
				break;
			case Protocol.YOUR_TURN:
				writeCommandAndData(Protocol.YOUR_TURN);
//				System.out.println("Send YOUR_TURN to player " + p.getId());
				break;
			case Protocol.SET_WANTED_STICKS:
				writeCommandAndData(Protocol.SET_WANTED_STICKS);
//				System.out.println("Send SET_WANTED_STICKS to player " + p.getId());
				Player tempPlayer = monitor.getCurrentSticker();
				writeCommandAndData(tempPlayer.getId());
				writeCommandAndData(tempPlayer.getWantedSticks());
				break;
			case Protocol.SET_STICKS:
				writeCommandAndData(Protocol.SET_STICKS);
//				System.out.println("Send SET_STICKS to player " + p.getId());
				break;
			case Protocol.SET_WINNER:
				writeCommandAndData(Protocol.SET_WINNER);
				writeCommandAndData(monitor.getWinner());
				break;
			case Protocol.PLAYER_LEFT:
				writeCommandAndData(Protocol.PLAYER_LEFT);
				return;
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
		writeCommandAndData(Protocol.NEW_GAME);
		System.out.println("Send NEW_GAME to player " + p.getId());
		writeCommandAndData(p.getId());
		writeCommandAndData(monitor.getNumberOfPlayers());
		writeCommandAndData(monitor.getNumberOfRounds());
	}

	private void startNewRound() {
		// send cards for new round
		writeCommandAndData(Protocol.NEW_ROUND);
		System.out.println("Send NEW_ROUND to player " + p.getId());
		int currRound = monitor.getRoundNumber();
		writeCommandAndData(currRound);
		for(int i=0; i<currRound; i++) {
			Card newCard = monitor.getNextCard();
			writeCommandAndData(newCard.getSuit());
			writeCommandAndData(newCard.getValue());
		}
	}
}
