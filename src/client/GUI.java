package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import protocol.Card;

public class GUI extends JFrame{

	private static final long serialVersionUID = 1L;

	private ArrayList<Card> currentHand = new ArrayList<Card>();

	private JPanel myCards;
	private JPanel middleCards;
	private JPanel trumfPanel;


	private boolean choiceCard = true;

	private Monitor monitor;

	private int nbrOfPlayers;

	private int myId;

	private int playedSuit;

	private int nbrOfPlayedCards;

	private Card trumf;

	private int totalSticks;
	public GUI(Monitor monitor) {
		setTitle("Plump");
		setSize(1366,768); // default size is 0,0
		setLayout(new BorderLayout());
		this.monitor = monitor;
		newGame(2, 5);
		addCardToHand(new Card(2,4));
		addCardToHand(new Card(6,2));
		addCardToHand(new Card(3,3));
		addNextPlayedCard(new Card(3,3),2);
		addNextPlayedCard(new Card(3,3),2);

		addNextPlayedCard(new Card(3,3),1);

		addNextPlayedCard(new Card(3,2),2);
		
		choiceNextCard();
		setTrumf(new Card(5,3));
	
		//getContentPane().add(myCards,BorderLayout.NORTH);
		//panel.add(null,BorderLayout.CENTER);
		//panel.add(new JLabel("Example"), BorderLayout.EAST);
		
	}

	public void setTrumf(Card card) {
		this.trumf = card;

		// change to create trumf card
		ImageIcon icon = createTrumfCard(card);

		JLabel label = new JLabel();
		label.setIcon(icon); 
		trumfPanel.add(label);
		getContentPane().add(trumfPanel,BorderLayout.WEST);

		revalidate();


		System.out.println("Trumf is: "+card.getSuit());
		// update GUI with Card.
	}
	public void addStick(int playerId) {
		System.out.println("Player: "+playerId+" get one stick");
		totalSticks = 0;
		// update GUI . +1 for player
	}

	public void setWantedSticks(int playerId, int sticks) {
		totalSticks += sticks;
		System.out.println("Player: "+playerId+" wants: "+sticks);

		// update GUI . with wanted sticks for player.

	}
	public void addCardToHand(Card card) {
		/*
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
		 */
		currentHand.add(card);

		ImageIcon icon = createCardOnHand(card);

		JLabel label = new JLabel();

		label.setIcon(icon); 
		label.addMouseListener(new CardListener(card,label));
		label.setHorizontalAlignment(JLabel.CENTER);
		myCards.add(label);
		
		getContentPane().add(myCards,BorderLayout.SOUTH);


	}


	public void addNextPlayedCard(Card card, int player) {
		//currentTurn.add(card);
		// Update gui with card for player... with boolean...
		if(nbrOfPlayedCards == nbrOfPlayers) {
			// TODO
			//gui clear all cards on the table.
			middleCards.removeAll();

			nbrOfPlayedCards = 0;
			playedSuit = card.getSuit();
		} 
		nbrOfPlayedCards++;


		ImageIcon icon = createCardInMiddle(card);

		JLabel label = new JLabel();
		label.setIcon(icon); 
		middleCards.add(label);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setVerticalAlignment(JLabel.CENTER);
		getContentPane().add(middleCards,BorderLayout.CENTER);
		revalidate();


		// add to card in middle, player.. 


	}

	public void cleanHand() {
		/*
		nbrOfSpades = 0;
		nbrOfHearts = 0;
		nbrOfDiamonds = 0;
		nbrOfClubs = 0;
		 */
		currentHand.clear();
		myCards.removeAll();

	}

	public void newGame(int id, int nbrOfPlayers) {
		myCards = new JPanel();
		
		myCards.setLayout(new FlowLayout());
		myCards.setAlignmentX(CENTER_ALIGNMENT);
		
		middleCards = new JPanel();
		middleCards.setLayout(new GridBagLayout());
		middleCards.setAlignmentX(CENTER_ALIGNMENT);
		middleCards.setAlignmentY(CENTER_ALIGNMENT);
		
		trumfPanel = new JPanel();
		trumfPanel.setLayout(new GridLayout(1,1));


		myId = id;
		this.nbrOfPlayers = nbrOfPlayers;
		nbrOfPlayedCards = nbrOfPlayers;
		// write gameplan in gui

		// TODO Auto-generated method stub
	}
	private String parseCardToPngString(Card card) {
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
		return path;
	}

	private ImageIcon createCardOnHand(Card card) {			
		return createImageIcon(parseCardToPngString(card), 83,121);
	}

	private ImageIcon createCardInMiddle(Card card) {			
		return createImageIcon(parseCardToPngString(card), 83,121);
	}

	private ImageIcon createTrumfCard(Card card) {			
		return createImageIcon(parseCardToPngString(card), 40,60);
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

					Thread t = new Thread() {
						public void run() {
							
							monitor.addNextCard(card);
						}
					};
					t.start();
					// Skapa en tråd som gör följande
					//monitor.addNextCard(card);
					currentHand.remove(card);
					myCards.remove(comp);

					revalidate();
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
					}
					if(hasSuit) {
						return;
					} else if(hasTrumf && card.getSuit() != trumf.getSuit()) {
						return;
					} else {
						Thread t = new Thread() {
							public void run() {
								monitor.addNextCard(card);
							}
						};
						t.start();		
						currentHand.remove(card);
						myCards.remove(comp);
						// send card and delete from view...
						revalidate();
						choiceCard = false;

					}
				}

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
	public void choiceNextCard() {
		System.out.println("Välj nästa kort!");
		choiceCard = true;
		// start timer here and put text.
	}

	public void setSticks() {
		// Ask for sticks and send to monitor with Thread...
		// monitor.
		// TODO Auto-generated method stub
		Scanner s = new Scanner(System.in);
		final int nbr = s.nextInt();
		Thread t = new Thread() {
			public void run() {
				monitor.addNumberOfSticks(nbr);
			}
		};
		t.start();		

		
	}

	public void setScore(int score, int playerId) {
		// TODO Auto-generated method stub
		System.out.println("Player: "+playerId+" . Score: "+score);

	}

}
