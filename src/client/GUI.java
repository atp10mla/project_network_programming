package client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import protocol.Card;

public class GUI extends JFrame{
	private ArrayList<Card> currentHand = new ArrayList<Card>();

	private Panel myCards;
	
	private boolean choiceCard = true;
	
	private int nbrOfPlayers;

	private int nbrOfSpades;
	private int nbrOfHearts;
	private int nbrOfDiamonds;
	private int nbrOfClubs;

	private int myId;

	private int playedSuit;

	private int nbrOfPlayedCards;

	private Card trumf;
	public GUI() {
		setTitle("Plump");
		setSize(1366,768); // default size is 0,0
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(1366,768));

		
		myCards = new Panel();
		myCards.setLayout(new GridLayout(1, 10));
		newGame(2, 3);
		addCardToHand(new Card(2,4));
		addCardToHand(new Card(6,2));
		addCardToHand(new Card(3,3));
		//getContentPane().add(myCards,BorderLayout.NORTH);
		//panel.add(null,BorderLayout.CENTER);
		//panel.add(new JLabel("Example"), BorderLayout.EAST);

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
		
		ImageIcon icon = createCardOnHand(card);
		
		//icon.
		
		JLabel label = new JLabel();
		label.setIcon(icon); 
		label.addMouseListener(new CardListener(card,label));
		myCards.add(label);
		getContentPane().add(myCards,BorderLayout.SOUTH);

		
		// Update GUI with new card...
	}


	public void addNextPlayedCard(Card card, int player) {
		//currentTurn.add(card);
		// Update gui with card for player... with boolean...
		if(nbrOfPlayedCards == nbrOfPlayers) {
			// TODO
			//gui clear all cards on the table.
			nbrOfPlayedCards = 0;
			playedSuit = card.getSuit();
		} 
		nbrOfPlayedCards++;

		
		// add to card in middle player.. 


	}

	public void cleanHand() {

		nbrOfSpades = 0;
		nbrOfHearts = 0;
		nbrOfDiamonds = 0;
		nbrOfClubs = 0;
		currentHand.clear();
		myCards = new Panel();
		myCards.setLayout(new GridLayout(1, 10));
		
	}

	public void newGame(int id, int nbrOfPlayers) {
		myId = id;
		this.nbrOfPlayers = nbrOfPlayers;
		nbrOfPlayedCards = nbrOfPlayers;
		// write gameplan in gui

		// TODO Auto-generated method stub
	}

	private ImageIcon createCardOnHand(Card card) {
		String path = "";
		switch (card.getValue()) {
		case Card.JACK:
			path+="jack_of_";
			break;
		case Card.QUEEN:
			path+="queen_of_";
			break;
		case Card.KING:
			path+="king_of_";
			break;
		case Card.ACE:
			path+="ace_of_";
			break;
		default:
			path+=card.getValue()+"_of_";
		}
		switch (card.getSuit()) {
		case Card.HEARTS:
			path+="hearts.png";
			break;
		case Card.CLUBS:
			path+="clubs.png";
			break;
		case Card.DIAMONDS:
			path+="diamonds.png";
			break;
		case Card.SPADES:
			path+="spades.png";
			break;
		}
		
		return createImageIcon(path, 83,121);
		
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	private ImageIcon createImageIcon(String path, int width, int height) {
		ImageIcon imgIcon = new ImageIcon("C:/Users/Markus/ProjectNetwork/Resources/"+path);
		Image img = imgIcon.getImage();
		img = img.getScaledInstance( width, height,  java.awt.Image.SCALE_SMOOTH ) ;  
		return new ImageIcon(img);
		
	}
	
	private class CardListener implements MouseListener {
		Card card;
		Component comp;
		public CardListener(Card card, Component comp) {
			this.card = card;
			this.comp = comp;
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			// TODO Auto-generated method stub
			// Check if ok to send card.
			if(choiceCard) {
				if(nbrOfPlayedCards==nbrOfPlayers || card.getSuit()== playedSuit) {
					currentHand.remove(card);
					myCards.remove(comp);
					System.out.println("get here!");
					getContentPane().add(myCards,BorderLayout.SOUTH);

					// send card and delete from view...
					choiceCard = false;
				} else {
					boolean hasTrumf = false;
					boolean hasSuit = false;
					
					for(Card card: currentHand) {
						if(card.getSuit()==trumf.getSuit()) {
							hasTrumf = true;
						}
						if(card.getSuit()==playedSuit) {
							hasSuit = true;
						}
						if(hasSuit) {
							return;
						} else if(hasTrumf && card.getSuit() != trumf.getSuit()) {
							return;
						} else {
							currentHand.remove(card);
							myCards.remove(comp);
							// send card and delete from view...
							choiceCard = false;
									
						}
					}
				}
				System.out.println(card);
			}
		}
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
		}
		@Override
		public void mousePressed(MouseEvent e) {
		}
		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}
}
