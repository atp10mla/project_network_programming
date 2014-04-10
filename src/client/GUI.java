package client;

import java.awt.BorderLayout;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import protocol.Card;

public class GUI extends JFrame{
	private ArrayList<Card> currentHand = new ArrayList<Card>();

	private int nbrOfSpades;
	private int nbrOfHearts;
	private int nbrOfDiamonds;
	private int nbrOfClubs;
	
	
	private Card trumf;
	public GUI() {
		setTitle("Plump");
		setSize(300,200); // default size is 0,0

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(100,100));

		/*
		InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("Resources/4_of_clubs.png");
				try {
				BufferedImage image = ImageIO.read(in);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		panel.add(null,BorderLayout.CENTER);
		panel.add(null,BorderLayout.CENTER);
		panel.add(new JLabel("Example"), BorderLayout.EAST);
		 */
	}

	public void setTrumf(Card card) {
		this.trumf = card;
		// update GUI with Card.
	}
	public void addStick(int playerId) {

		// update GUI . +1 for player
	}

	public void setWantedSticks(int playerId, int sticks) {

		// update GUI . with wanted sticks for player.

	}
	public void addCardToHand(Card card) {
		switch(card.getSuit()) {
		case Card.CLUBS:
			nbrOfClubs++;
			break;
		case Card.HEARTS:
			nbrOfHearts++;
			break;
		case Card.DIAMONDS:
			nbrOfDiamonds++;
			break;
		case Card.SPADES:
			nbrOfSpades++;
			break;
		}
		currentHand.add(card);
		// Update GUI with new card...
	}


	public void cleanHand() {
		
		nbrOfSpades = 0;
		nbrOfHearts = 0;
		nbrOfDiamonds = 0;
		nbrOfClubs = 0;
		currentHand.clear();

	}


}
