package client;

import java.util.HashSet;

import protocol.Card;
import protocol.Protocol;

public class GUIWriterThread extends Thread {


	private Monitor monitor;

	public GUIWriterThread(Monitor monitor) {
		this.monitor = monitor;
	}

	public void run() {

		while(true) {
			int cmd = monitor.getNextGUIEvent();

			switch(cmd) {
			case Protocol.PLAYED_CARD: //View played card
				
				break;
			case Protocol.YOUR_TURN:
				// Show timer on GUI
				break;
			case Protocol.NEW_ROUND:
				// set all sticks to 0 in GUI
				// show players card
				HashSet<Card> cards = monitor.getCurrentHand();
				for(Card card:cards) {
					// show card on GUI..
				}
				break;
			
				
			}


		}	




	}

}
