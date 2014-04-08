package client;

import java.io.IOException;
import java.io.InputStream;

import protocol.Card;
import protocol.Protocol;

public class ISHandler extends Thread{

	private InputStream is;
	private Monitor monitor;
	public ISHandler(InputStream is, Monitor monitor) {
		this.is = is;
		this.monitor = monitor;
	}
	
	public void run() {
		try {
			while(true) {
				int cmd = is.read();
				switch(cmd) {
				case Protocol.YOUR_TURN:
					monitor.choiceNextCard();
					break;
				case Protocol.PLAYED_CARD:
					Card card = new Card(is.read(),is.read());
					monitor.addNextPlayedCard(card);
					break;
				case Protocol.NEW_ROUND:
					monitor.cleanHand();
					int nbrOfCards = is.read();
					for(int i = 0;i < nbrOfCards;i++) {
						card = new Card(is.read(),is.read());
						monitor.addCardToHand(card);
					}
					break;
				case Protocol.SET_TRUMF:
					monitor.setTrumf(is.read());
					break;
				case Protocol.STICK_WINNER:
					monitor.addStick(is.read());
					break;
				case Protocol.SET_WANTED_STICKS:
					int player = is.read();
					monitor.addWantedSticks(player,is.read());
					break;
				
				}
			
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	
	}
	
}
