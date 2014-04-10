package server;

import java.io.IOException;
import java.io.InputStream;

import protocol.Card;
import protocol.Protocol;

public class InputHandler extends Thread {
	private InputStream is;
	private Monitor monitor;
	private Player p;

	public InputHandler(InputStream is, Monitor monitor, Player p) {
		this.is = is;
		this.monitor = monitor;
		this.p = p;
	}
	
	public void run() {
		while(true) {
			int com = 0;
			try {
				com = is.read();
			} catch (IOException e) {
				System.out.println("Could not read from is");
				System.exit(1);
			}
			
			switch (com) {
			case Protocol.GET_DELAY: {
				monitor.sendCommando(Protocol.SEND_MORE_TIME);
			}
			case Protocol.SEND_CARD: {
				monitor.sendCommando(Protocol.PLAYED_CARD);
				int suit = readOneCommand(is);
				int value = readOneCommand(is);
				monitor.sendPlayedCard(new Card(suit, value, p));
			}
			case Protocol.SET_WANTED_STICKS: {
				monitor.sendCommando(Protocol.SET_WANTED_STICKS);
				int playerNbr = readOneCommand(is);
				int wishNbrOfSticks = readOneCommand(is);
				monitor.sendCommando(playerNbr);
				monitor.sendCommando(wishNbrOfSticks);
			}
			case Protocol.SET_STICKS:
				monitor.setWantedSticks(readOneCommand(is), p);
				break;
			default: {
				// some error
			}
			}
			
		}
		
	}
	
	private int readOneCommand(InputStream is) {
		try {
			return is.read();
		} catch (IOException e) {
			System.out.println("Could not read one command ih");
			System.exit(1);
		}
		return 0;
	}

}
