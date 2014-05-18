package client;
import java.io.IOException;
import java.io.InputStream;

import protocol.Card;
import protocol.Protocol;

public class ISHandler extends Thread{
	private InputStream is;
	private GUI gui;
	public ISHandler(InputStream is, GUI gui) {
		this.is = is;
		this.gui = gui;
	}
	
	public void run() {
		try {
			while(true) {
				int cmd = is.read();
				switch(cmd) {
				case Protocol.NEW_GAME:
					System.out.println("GET NEW GAME");
					int id = is.read();
					int nbrOfPlayers = is.read();
					gui.newGame(id, nbrOfPlayers);
					break;				
				case Protocol.NEW_ROUND:
					System.out.println("GET NEW ROUND");
					int nbrOfCards = is.read();
					gui.cleanHand();
					for(int i = 0;i < nbrOfCards;i++) {
						int suit = is.read();
						int value = is.read();
						Card card = new Card(suit, value);
						gui.addCardToHand(card);
					}
					gui.createIconsForCardsOnHand();
					break;
				case Protocol.SET_TRUMF:				
					System.out.println("GET SET TRUMF");
					gui.setTrumf(new Card(is.read(),is.read()));
					break;
				case Protocol.SET_STICKS:
					System.out.println("GET SET STICKS");
					gui.setSticks();		
					break;
				case Protocol.YOUR_TURN:
					System.out.println("GET YOUR TURN");
					gui.chooseNextCard();
					break;
				case Protocol.PLAYED_CARD:
					System.out.println("GET PLAYED CARD");
					int player = is.read();
					Card card = new Card(is.read(),is.read());
					gui.addNextPlayedCard(card, player);
					break;
				case Protocol.STICK_WINNER:
					System.out.println("GET STICK WINNER");
					gui.addStick(is.read());
					break;
				case Protocol.SET_WANTED_STICKS:
					System.out.println("GET SET WANTED STICKS");
					gui.setWantedSticks(is.read(),is.read());
					break;
				case Protocol.ROUND_SCORE:
					System.out.println("GET ROUND SCORE");
					nbrOfPlayers = is.read();
					for(int i = 1;i<=nbrOfPlayers;i++) {
						int score = is.read();
						gui.setScore(score,i);
					}
					break;
				}
			}
		} catch (IOException e) {		
		}
	}
}
