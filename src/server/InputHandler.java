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
			int com = readOneCommand(is);
			System.out.println("Read : " + com + " from player " + p.getId());
			
			switch (com) {
			case Protocol.SEND_CARD: { // 1
				int suit = readOneCommand(is);
				int value = readOneCommand(is);
				System.out.println("Read card: suit = " + suit + " value = " + value);
				monitor.sendPlayedCard(new Card(suit, value, p));
				break;
			}
			case Protocol.SET_STICKS:
				System.out.println("waiting for set sticks");
				monitor.setWantedSticks(readOneCommand(is), p);
				System.out.println("read set sticks");
				break;
			case Protocol.GET_DELAY: {
				monitor.sendCommando(Protocol.SEND_MORE_TIME);
				break;
			}
			default: {
				System.out.println(" -- unexpected input");
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
