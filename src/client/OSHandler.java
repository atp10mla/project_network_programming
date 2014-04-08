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

				switch(cmd) {
				case Protocol.SEND_CARD:
					Card card = monitor.getNextCard();
					os.write(Protocol.SEND_CARD);
					os.write(card.getSuit());
					os.write(card.getValue());
					os.write('\n');
					break;
				case Protocol.GET_DELAY:
					os.write(Protocol.GET_DELAY);
					os.write('\n');
					break;
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}

	}

}
