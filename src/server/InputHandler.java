package server;

import java.io.IOException;
import java.io.InputStream;

import protocol.Protocol;

public class InputHandler extends Thread {

	private InputStream is;
	private Monitor monitor;

	public InputHandler(InputStream is, Monitor monitor) {
		this.is = is;
		this.monitor = monitor;
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
			case Protocol.SEND_CARD: {
				
			}
			case Protocol.GET_DELAY: {
				
			}
			case Protocol.PLAYED_CARD: {
				monitor.sendCommando(Protocol.SEND_CARD);
				int suit = readOneCommand(is);
				int value = readOneCommand(is);
				monitor.sendCommando(suit);
				monitor.sendCommando(value);
			}
			case Protocol.SET_TRUMF: {
				monitor.setTrumf(readOneCommand(is));
			}
			case Protocol.SET_WANTED_STICKS: {
				monitor.sendCommando(Protocol.SET_WANTED_STICKS);
				int playerNbr = readOneCommand(is);
				int wishNbrOfSticks = readOneCommand(is);
				monitor.sendCommando(playerNbr);
				monitor.sendCommando(wishNbrOfSticks);
			}
			case Protocol.STICK_WINNER: {
				monitor.sendCommando(Protocol.STICK_WINNER);
				int playerNbr = readOneCommand(is);
				monitor.sendCommando(playerNbr);
			}
			
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
