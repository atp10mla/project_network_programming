package client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
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

	// Current cards on hand
	private ArrayList<Card> currentHand = new ArrayList<Card>();

	// Message element (wait for next player, set sticks...)
	private JLabel textMessage = new JLabel();

	// True if your turn to set sticks.
	private boolean setSticks = false;

	// Vector with JLabels with sticks taken this round for each player.
	private JLabel takenSticks[];

	// Hard coded nbr of rounds. shift to set by server.
	private int nbrOfRounds = 3;
	private int sticksInRound = nbrOfRounds + 1;


	// JPanels
	private JPanel myCards;
	private JPanel middleCards;
	private JPanel trumfPanel;

	// Score board
	private JLabel[][] scoreBoard;

	// True if it is your turn to choice a card.
	private boolean choiceCard = false;

	// The monitor
	private Monitor monitor;
	private int nbrOfPlayers;

	// First played card has suit.
	private int playedSuit;

	// Nbr of played cards in a stick
	private int nbrOfPlayedCards;

	// Trumf card for the round
	private Card trumf;
	private int roundNbr;

	// For stick selection
	private JSpinner spinner;
	private JButton sendSticks;

	private int playersSetSticks;

	private int totalSticks;

	private int dir = -1;

	/**
	 * 
	 * @param monitor
	 */
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

	/**
	 * Set the trumf card for the current round.
	 * @param card The trumf card
	 */
	public void setTrumf(Card card) {
		this.trumf = card;
		trumfPanel.removeAll();
		ImageIcon icon = createTrumfCard(card);
		JLabel label = new JLabel();
		label.setIcon(icon); 
		trumfPanel.add(label);
		// Dont know if i need this row...
		getContentPane().add(trumfPanel,BorderLayout.WEST);
		revalidate();
	}

	/**
	 * Add one to the stick winner.
	 * @param playerId id of stick winner.
	 */
	public void addStick(int playerId) {
		System.out.println("Player: "+playerId+" get one stick");
		takenSticks[playerId-1].setText(""+(Integer.parseInt(takenSticks[playerId-1].getText())+1));
		middleCards.removeAll();
		revalidate();
		// update GUI . +1 for player
	}

	/**
	 * Set wanted sticks for a player.
	 * @param playerId The player
	 * @param sticks Number of sticks
	 */
	public void setWantedSticks(int playerId, int sticks) {
		totalSticks += sticks;
		playersSetSticks++;
		System.out.println("Player: "+playerId+" wants: "+sticks);
		scoreBoard[roundNbr][playerId].setText(sticks+"");
		revalidate();
	}

	/**
	 * Add a card to the hand.
	 * @param card The card
	 */
	public void addCardToHand(Card card) {
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


	/**
	 * Add next played card in the middle
	 * @param card The card
	 * @param player The player who played the card.
	 */
	public void addNextPlayedCard(Card card, int player) {

		if(nbrOfPlayedCards == nbrOfPlayers) {
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
		// Need this?? TODO
		getContentPane().add(middleCards,BorderLayout.CENTER);
		revalidate();
	}

	/**
	 * Clean the hand. New round starts.
	 */
	public void cleanHand() {
		
		if(dir == -1) {
			if(sticksInRound != 1) {
				sticksInRound--;
			} else {
				dir = 1;
			}
		} else {
			sticksInRound++;
		}
		
		totalSticks = 0;
		playersSetSticks = 0;
		for(int i = 0;i<takenSticks.length;i++) {
			takenSticks[i].setText("0");
		}
		roundNbr++;
		currentHand.clear();
		myCards.removeAll();
		revalidate();
	}

	/**
	 * Start a new game with a given number of players.
	 * @param id The id you have.
	 * @param nbrOfPlayers Number of players
	 */
	public void newGame(int id, final int nbrOfPlayers) {
		this.nbrOfPlayers = nbrOfPlayers;
		createScoreBoard(id);
		createStickPanel();
		roundNbr = 0;
		myCards = new JPanel();
		myCards.setLayout(new GridLayout(1,nbrOfRounds));
		middleCards = new JPanel();
		middleCards.setLayout(new GridBagLayout());
		middleCards.setAlignmentX(CENTER_ALIGNMENT);
		middleCards.setAlignmentY(CENTER_ALIGNMENT);
		trumfPanel = new JPanel();
		nbrOfPlayedCards = nbrOfPlayers;
		revalidate();
	}
	private void createStickPanel() {
		JPanel sticks = new JPanel();
		sendSticks = new JButton("Send sticks");
		sendSticks.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				if(setSticks) {
					if(playersSetSticks == nbrOfPlayers-1 && (int)spinner.getValue() + totalSticks == sticksInRound) {
						textMessage.setText("Total sticks +  your sticks = total sticks in the round. Error");
						revalidate();
						return;
					}
					Thread t = new Thread() {
						public void run() {
							setSticks = false;
							System.out.println("count: "+ spinner.getValue());
							monitor.addNumberOfSticks((int)spinner.getValue());
							textMessage.setText("Wait for other players...");
							revalidate();
						}
					};
					t.start();		
				}
			}
		});
		spinner = new JSpinner( new SpinnerNumberModel( 1,0,nbrOfRounds,1 ) );
		sticks.add(spinner);
		sticks.add(sendSticks);
		sticks.add(textMessage);
		JPanel panelTakenSticks = new JPanel();
		panelTakenSticks.setLayout(new BorderLayout(2,3));

		takenSticks = new JLabel[nbrOfPlayers];
		for(int j=0;j<2;j++) {
			for(int i = 0;i<nbrOfPlayers;i++) {
				if(j==0) {
					System.out.println("add player"+i);
					panelTakenSticks.add(new JLabel("Player "+i));

				} else {
					takenSticks[i] = new JLabel("0");
					panelTakenSticks.add(takenSticks[i]);

				}
			}
		}
		trumfPanel.add(panelTakenSticks);
		getContentPane().add(sticks,BorderLayout.NORTH);

	}

	private void createScoreBoard(int id) {
		JPanel panelScoreBoard = new JPanel();
		panelScoreBoard.setLayout(new GridLayout(nbrOfRounds*2+1,nbrOfPlayers+1));
		scoreBoard =  new JLabel[nbrOfRounds*2+1][nbrOfPlayers+1];
		for(int i = 0;i<nbrOfRounds*2+1;i++) {
			for(int j=0;j<=nbrOfPlayers;j++){
				if(j==0) {
					if(i!=0) {
						if(i<=nbrOfRounds) {
							scoreBoard[i][j] = new JLabel(""+(nbrOfRounds+1-i));
						} else {
							scoreBoard[i][j] = new JLabel(""+(i-nbrOfRounds));	
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


	}
	
	/**
	 * Parse card to png file.
	 * @param card The card to parse.
	 * @return
	 */
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
		return createImageIcon(parseCardToPngString(card), 60,90);
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	private ImageIcon createImageIcon(String path, int width, int height) {
		// TODO find correct folder
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
							textMessage.setText("Wait for other players");
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
								textMessage.setText("Wait for other players");
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
	
	/**
	 * Tells player that it is its turn to choice a card.
	 */
	
	public void choiceNextCard() {
		choiceCard = true;
		// start timer here and put text.
		textMessage.setText("Choice next card!!!!!");
		revalidate();

	}

	/**
	 * Tells player that it is its turn to choice the number of sticks.
	 */
	public void setSticks() {
		textMessage.setText("Set numer of sticks!");
		revalidate();
		setSticks = true;
	}

	/**
	 * Set the score for player 
	 * @param score The score
	 * @param playerId The player
	 */
	public void setScore(int score, int playerId) {
		scoreBoard[roundNbr][playerId].setText(score+"");
		revalidate();
		
	}

	/**
	 * Update screen then finish dealing. 
	 */
	public void finishDealing() {
		getContentPane().add(myCards,BorderLayout.SOUTH);
		revalidate();
	}

}
