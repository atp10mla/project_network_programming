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
				case Protocol.YOUR_TURN:

					System.out.println("YOUR TURN");
					gui.choiceNextCard();
					break;
				case Protocol.PLAYED_CARD:

					System.out.println("PLAYED CARD");
					int player = is.read();
					Card card = new Card(is.read(),is.read());
					gui.addNextPlayedCard(card, player);
					break;
				case Protocol.NEW_ROUND:
					System.out.println("Start new round");
					gui.cleanHand();
					int nbrOfCards = is.read();
					System.out.println("nbr of cards "+nbrOfCards);
					for(int i = 0;i < nbrOfCards;i++) {
						card = new Card(is.read(),is.read());
						gui.addCardToHand(card);
					}
					gui.finishDealing();
					break;
				case Protocol.SET_TRUMF:				
					System.out.println("get trumf");
					gui.setTrumf(new Card(is.read(),is.read()));
					
					//monitor.setTrumf(new Card(is.read(),is.read()));
					break;
				case Protocol.STICK_WINNER:
					gui.addStick(is.read());
					// +1 stick winner GUI 
					//monitor.addStick(is.read()); 
					break;
				case Protocol.SET_WANTED_STICKS:

					System.out.println("SET WANTED STICKS");
					gui.setWantedSticks(is.read(),is.read());
					// set stick to player in GUI.
					
					//monitor.addWantedSticks(player,is.read());
					break;
				case Protocol.ROUND_SCORE:
					
					System.out.println("ROUND SCORE");
					int nbrOfPlayers = is.read();
					for(int i = 1;i<=nbrOfPlayers;i++) {
						int score = is.read();
						// add score for player i
						gui.setScore(score,i);
						//monitor.playerScore(i,is.read());
					}
					
					//monitor.updateScore();
					break;
				case Protocol.NEW_GAME:
					System.out.println("Start new game...");
					int id = is.read();
					nbrOfPlayers = is.read();
					gui.newGame(id, nbrOfPlayers);
					break;
				
				case Protocol.SET_STICKS:
					// Also start timer...
					
					gui.setSticks();
					
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	
	}
	
}
