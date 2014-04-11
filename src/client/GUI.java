package client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import protocol.Card;

public class GUI extends JFrame{

	private static final long serialVersionUID = 1L;

	private ArrayList<Card> currentHand = new ArrayList<Card>();

	private JLabel textMessage = new JLabel();
	
	private boolean setSticks = false;
	
	private JPanel myCards;
	private JPanel middleCards;
	private JPanel trumfPanel;

	private JPanel panelScoreBoard;

	private JLabel[][] scoreBoard;

	private boolean choiceCard = true;

	private Monitor monitor;

	private int nbrOfPlayers;

	private int myId;

	private int playedSuit;

	private int nbrOfPlayedCards;

	private Card trumf;
	
	private int roundNbr;

	private JSpinner spinner;
	private JButton sendSticks;
	
	private int wantedSticks= -1;
	
	private int totalSticks;
	public GUI(Monitor monitor) {
		setTitle("Plump");
		setSize(1366,768); // default size is 0,0
		setLayout(new BorderLayout());
		this.monitor = monitor;
		/*
		newGame(2, 5);
		cleanHand();
		setWantedSticks(2, 3);
		setScore(13, 1);
		addCardToHand(new Card(2,4));
		addCardToHand(new Card(6,2));
		addCardToHand(new Card(3,3));
		addNextPlayedCard(new Card(3,3),2);
		addNextPlayedCard(new Card(3,3),2);

		addNextPlayedCard(new Card(3,3),1);

		addNextPlayedCard(new Card(3,2),2);

		choiceNextCard();
		setTrumf(new Card(5,3));
		*/
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
		totalSticks++;
		middleCards.removeAll();
		revalidate();
		// update GUI . +1 for player
	}

	public void setWantedSticks(int playerId, int sticks) {
		totalSticks += sticks;
		
		System.out.println("Player: "+playerId+" wants: "+sticks);

		scoreBoard[roundNbr][playerId].setText(sticks+"");
		revalidate();
		// update GUI . with wanted sticks for player.

	}
	public void addCardToHand(Card card) {

		System.out.println("Suit: "+ card.getSuit()+" Value: "+card.getValue() );
		currentHand.add(card);

		ImageIcon icon = createCardOnHand(card);
		JLabel label = new JLabel();

		label.setIcon(icon); 
		label.addMouseListener(new CardListener(card,label));
		label.setHorizontalAlignment(JLabel.CENTER);
		myCards.add(label);

		
		/*
		currentHand.add(card);

		ImageIcon icon = createCardOnHand(card);

		JLabel label = new JLabel();

		label.setIcon(icon); 
		label.addMouseListener(new CardListener(card,label));
		label.setHorizontalAlignment(JLabel.CENTER);
		myCards.add(label);
		getContentPane().add(myCards,BorderLayout.SOUTH);
*/

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
		roundNbr++;
		currentHand.clear();
		myCards.removeAll();

	}

	public void newGame(int id, int nbrOfPlayers) {

		panelScoreBoard = new JPanel();
		panelScoreBoard.setLayout(new GridLayout(21,nbrOfPlayers+1));
		scoreBoard =  new JLabel[21][nbrOfPlayers+1];
		for(int i = 0;i<21;i++) {
			for(int j=0;j<=nbrOfPlayers;j++){
				if(j==0) {
					if(i!=0) {
						if(i<=10) {
							scoreBoard[i][j] = new JLabel(""+(11-i));
						} else {
							scoreBoard[i][j] = new JLabel(""+(i-10));	
						}
					} else {
						scoreBoard[i][j] = new JLabel("");
					}

				} else {
					if(i==0) {
						if(id==j) {
							scoreBoard[i][j] = new JLabel(""+j +"(you) ");
						} else {
							scoreBoard[i][j] = new JLabel(""+j);
						}
					} else {
						scoreBoard[i][j] = new JLabel("");
					}


				}

				panelScoreBoard.add(scoreBoard[i][j]);
			}
		}
		getContentPane().add(panelScoreBoard,BorderLayout.EAST);
		revalidate();

		roundNbr = 0;
		
		myCards = new JPanel();
		myCards.setLayout(new GridLayout(1,10));
		

		middleCards = new JPanel();
		middleCards.setLayout(new GridBagLayout());
		middleCards.setAlignmentX(CENTER_ALIGNMENT);
		middleCards.setAlignmentY(CENTER_ALIGNMENT);

		trumfPanel = new JPanel();
		trumfPanel.setLayout(new GridLayout(1,1));


		myId = id;
		this.nbrOfPlayers = nbrOfPlayers;
		nbrOfPlayedCards = nbrOfPlayers;
	
		JPanel sticks = new JPanel();
		sendSticks = new JButton("Send sticks");
		sendSticks.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent event) {
				if(setSticks) {
					setSticks = false;
					Thread t = new Thread() {
						public void run() {
							monitor.addNumberOfSticks(spinner.getComponentCount());
						}
					};
					t.start();		
					
				}
				
				// TODO Auto-generated method stub
				//if(event.)
				
			}
		});
		spinner = new JSpinner( new SpinnerNumberModel( 1,1,10,1 ) );
	    sticks.add(spinner);
	    sticks.add(sendSticks);
	    sticks.add(textMessage);
	    getContentPane().add(sticks,BorderLayout.NORTH);
	    
		
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
		System.out.println(path);
		return path;
	}

	private ImageIcon createCardOnHand(Card card) {			
		return createImageIcon(parseCardToPngString(card), 83,121);
	}

	private ImageIcon createCardInMiddle(Card card) {			
		return createImageIcon(parseCardToPngString(card), 83,121);
	}

	private ImageIcon createTrumfCard(Card card) {			
		return createImageIcon(parseCardToPngString(card), 60,90);
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
							System.out.println("send to monitor");
							textMessage.setText("");
							revalidate();
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
								System.out.println("send to monitor");
								textMessage.setText("");
								revalidate();
								monitor.addNextCard(card);
							}
						};
						t.start();
						
						currentHand.remove(card);
						myCards.remove(comp);

						revalidate();
						// send card and delete from view...
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
		textMessage.setText("Välj nästa kort");
		revalidate();
		
	}

	public void setSticks() {
		// Ask for sticks and send to monitor with Thread...
		// monitor.
		// TODO Auto-generated method stub
		
	//sendSticks.
		setSticks = true;
		

	}

	public void setScore(int score, int playerId) {
		// TODO Auto-generated method stub
		System.out.println("Player: "+playerId+" . Score: "+score);

		scoreBoard[roundNbr][playerId].setText(score+"");
		revalidate();
		
	}

	public void finishDealing() {
		// TODO Auto-generated method stub
		getContentPane().add(myCards,BorderLayout.SOUTH);
		revalidate();
		
	}

}
