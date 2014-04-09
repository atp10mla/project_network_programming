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
				break;
			case Protocol.PLAYED_CARD:
				Card c = monitor.getNextPlayedCard(p);
				try {
					os.write(c.getOwner().getId());
					os.write(c.getSuit());
					os.write(c.getValue());
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case Protocol.ROUND_SCORE:

				break;
			case Protocol.SET_TRUMF:

				break;
			case Protocol.STICK_WINNER:

				break;
			case Protocol.YOUR_TURN:

				break;



			}


			//		} catch (IOException e) {
			//		System.out.println("Could not write from os");
			//	System.exit(1);
			//}
		}

	}

	private void makeNewGame() {
		// send players
		// send cards for new round
	}

	private void startNewRound() {

	}



}
