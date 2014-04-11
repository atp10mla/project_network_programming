package client;

import java.io.IOException;
import java.io.OutputStream;

import protocol.Card;
import protocol.Protocol;

public class OSHandler extends Thread{

	private OutputStream os;
	private Monitor monitor;
	public OSHandler(OutputStream os, Monitor monitor) {
		this.os = os;
		this.monitor = monitor;
	}

	public void run() {
		try {
			while(true) {
				int cmd = monitor.getNextCommando();
				System.out.println("Next commande sended!: "+cmd);
				switch(cmd) {
				case Protocol.SEND_CARD: // Send next card.
					System.out.println("Skickar mitt kort till servern");
					Card card = monitor.getNextCard();
					System.out.println(card.getSuit()+" "+card.getValue());

					os.write(Protocol.SEND_CARD);
					os.write(card.getSuit());
					os.write(card.getValue());	
					break;
				case Protocol.GET_DELAY:
					os.write(Protocol.GET_DELAY);
					break;
				case Protocol.SET_STICKS:
					
					
					System.out.println("Sä");
					int nbrOfSticks = monitor.getNumberOfSticks();
					os.write(Protocol.SET_STICKS);
					os.write(nbrOfSticks);
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	}
}
