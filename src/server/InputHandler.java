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
				System.out.println("Read : " + com + " from player " + p.getId());
			} catch (IOException e) {
				System.out.println("Could not read from is");
				System.exit(1);
			}
			
			switch (com) {
			case Protocol.SEND_CARD: { // 1
				int suit = readOneCommand(is);
				int value = readOneCommand(is);
				monitor.sendPlayedCard(new Card(suit, value, p));
				
			}
			case Protocol.GET_DELAY: {
				monitor.sendCommando(Protocol.SEND_MORE_TIME);
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
