package client;

import java.io.IOException;
import java.io.InputStream;

import protocol.Card;
import protocol.Protocol;

public class ISHandler extends Thread{


	private InputStream is;
	private Monitor monitor;
	private GUI gui;
	public ISHandler(InputStream is, Monitor monitor, GUI gui) {
		this.is = is;
		this.monitor = monitor;
		this.gui = gui;
	}
	
	public void run() {
		try {
			while(true) {
				int cmd = is.read();
				switch(cmd) {
				case Protocol.YOUR_TURN:
					gui.choiceNextCard();
					break;
				case Protocol.PLAYED_CARD:
					Card card = new Card(is.read(),is.read());
					int player = is.read();
					gui.addNextPlayedCard(card, player);
					break;
				case Protocol.NEW_ROUND:
					gui.cleanHand();
					int nbrOfCards = is.read();
					for(int i = 0;i < nbrOfCards;i++) {
						card = new Card(is.read(),is.read());
						gui.addCardToHand(card);
					}
					break;
				case Protocol.SET_TRUMF:				
					gui.setTrumf(new Card(is.read(),is.read()));
					
					//monitor.setTrumf(new Card(is.read(),is.read()));
					break;
				case Protocol.STICK_WINNER:
					gui.addStick(is.read());
					// +1 stick winner GUI 
					//monitor.addStick(is.read()); 
					break;
				case Protocol.SET_WANTED_STICKS:
	
					gui.setWantedSticks(is.read(),is.read());
					// set stick to player in GUI.
					
					//monitor.addWantedSticks(player,is.read());
					break;
				case Protocol.ROUND_SCORE:
					int nbrOfPlayers = is.read();
					for(int i = 1;i<=nbrOfPlayers;i++) {
						int score = is.read();
						// add score for player i
						
						//monitor.playerScore(i,is.read());
					}
					//monitor.updateScore();
					break;
				case Protocol.NEW_GAME:
					int id = is.read();
					nbrOfPlayers = is.read();
					gui.newGame(id, nbrOfPlayers);
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	
	}
	
}
