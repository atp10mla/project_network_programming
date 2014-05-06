package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import protocol.Card;

public class GUI extends JFrame{
	private TimerThread waitThread;

	private TimerThread stickThread;

	private TimerThread nextCardThread;

	private Color backgroundGreen = new Color(78,222,97);

	private static final long serialVersionUID = 1L;

	private JLabel labelWait;

	// Current cards on hand
	private ArrayList<Card> currentHand = new ArrayList<Card>();

	// Message element (wait for next player, set sticks...)
	private JLabel textMessage = new JLabel("Wait for other players");

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
		setSize(800,600); // default size is 0,0
		setLayout(new BorderLayout());
		this.monitor = monitor;
		labelWait = new JLabel("Wait for players: ");

		labelWait.setHorizontalAlignment(SwingConstants.CENTER);
		labelWait.setVerticalAlignment(SwingConstants.CENTER);
		labelWait.setFont(new Font(labelWait.getFont().getFontName(),Font.PLAIN,30));
		getContentPane().setBackground(backgroundGreen);

		getContentPane().add(labelWait);		
		waitThread = new TimerThread(60*1000*2, labelWait,(GUI)null); 
		waitThread.setTextBefore(labelWait.getText());
		waitThread.start();
		/*
		newGame(1, 2);
		cleanHand();
		setWantedSticks(2, 3);
		setScore(13, 1);
		addCardToHand(new Card(Card.HEARTS,2));
		addCardToHand(new Card(Card.DIAMONDS,6));
		addCardToHand(new Card(Card.CLUBS,3));
		addCardToHand(new Card(Card.DIAMONDS,2));
		addCardToHand(new Card(Card.CLUBS,2));
		addCardToHand(new Card(Card.DIAMONDS,9));
		addCardToHand(new Card(Card.CLUBS,9));
		finishDealing();

		addNextPlayedCard(new Card(3,3),1);
		addNextPlayedCard(new Card(3,2),1);

		choiceNextCard();
		setTrumf(new Card(3,5));

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
		trumfPanel.add(new JLabel("Trumf"));
		trumfPanel.add(label);
		// Dont know if i need this row...
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
		middleCards.revalidate();
		repaint();
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
		scoreBoard[roundNbr+1][playerId].setText(sticks+"");
		revalidate();
	}

	/**
	 * Add a card to the hand.
	 * @param card The card
	 */
	public void addCardToHand(Card card) {
		currentHand.add(card);

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

		label.setBorder(BorderFactory.createLineBorder(Color.BLACK,1,true));
		middleCards.add(label);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setVerticalAlignment(JLabel.CENTER);
		// Need this?? TODO

		middleCards.revalidate();
		repaint();
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
		//myCards.addTransparentCard();
		revalidate();
	}

	/**
	 * Start a new game with a given number of players.
	 * @param id The id you have.
	 * @param nbrOfPlayers Number of players
	 */
	public void newGame(int id, final int nbrOfPlayers) {
		if(waitThread.isAlive()) {
			waitThread.kill();
		}

		this.remove(labelWait);
		this.nbrOfPlayers = nbrOfPlayers;
		createScoreBoard(id);
		createUpperLayout();
		trumfPanel = new JPanel();
		trumfPanel.setLayout(new BoxLayout(trumfPanel,BoxLayout.Y_AXIS));
		trumfPanel.setBackground(backgroundGreen);
		trumfPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		roundNbr = 0;
		myCards = new JPanel();
		myCards.setBorder(new EmptyBorder(10, 10, 10, 10));
		myCards.setBackground(backgroundGreen);
		myCards.setLayout(new FlowLayout());
		//myCards.setLayout(new GridLayout(1,nbrOfRounds));
		middleCards = new JPanel();
		middleCards.setBorder(BorderFactory.createLineBorder(Color.BLACK,5,false));
		middleCards.setLayout(new GridBagLayout());
		middleCards.setAlignmentX(CENTER_ALIGNMENT);
		middleCards.setAlignmentY(CENTER_ALIGNMENT);
		middleCards.setBackground(new Color(214,193,75));
		getContentPane().add(middleCards,BorderLayout.CENTER);
		getContentPane().add(myCards,BorderLayout.SOUTH);
		getContentPane().add(trumfPanel,BorderLayout.WEST);
		nbrOfPlayedCards = nbrOfPlayers;
		revalidate();
	}
	private void createUpperLayout() {
		JPanel upper = new JPanel();

		upper.setSize(100, 50);
		upper.setLayout(new BoxLayout(upper,BoxLayout.Y_AXIS));

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
							if(stickThread.isAlive()) {
								stickThread.kill();
							}
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

		JPanel panelTakenSticks = new JPanel();
		//		panelTakenSticks.setLayout(new BorderLayout(2,nbrOfPlayers));
		panelTakenSticks.setLayout(new GridBagLayout());
		panelTakenSticks.setBorder(new EmptyBorder(10, 10, 10, 10));
		GridBagConstraints c = new GridBagConstraints();
		//natural height, maximum width





		takenSticks = new JLabel[nbrOfPlayers];
		for(int j=0;j<2;j++) {
			for(int i = 0;i<nbrOfPlayers;i++) {
				if(j==0) {
					System.out.println("add player"+i);
					c.weightx = 0.5;
					c.fill = GridBagConstraints.HORIZONTAL;
					c.gridx = i;
					c.gridy = 0;

					panelTakenSticks.add(new JLabel("Player "+(i+1)),c);

				} else {
					takenSticks[i] = new JLabel("0");

					c.fill = GridBagConstraints.HORIZONTAL;
					c.gridx = i;
					c.gridy = 11;

					panelTakenSticks.add(takenSticks[i],c);

				}
			}
		}
		upper.setBackground(backgroundGreen);
		sticks.setBackground(backgroundGreen);
		panelTakenSticks.setBackground(backgroundGreen);
		upper.add(sticks);
		upper.add(textMessage);
		textMessage.setFont(new Font(textMessage.getFont().getFontName(),Font.PLAIN,20));
		textMessage.setForeground(Color.RED);
		textMessage.setAlignmentX(CENTER_ALIGNMENT);
		textMessage.setBorder(new EmptyBorder(2, 2, 2, 2));

		upper.add(panelTakenSticks);
		getContentPane().add(upper,BorderLayout.NORTH);

	}

	private void createScoreBoard(int id) {
		JPanel panelScoreBoard = new JPanel();
		panelScoreBoard.setLayout(new GridLayout(nbrOfRounds*2+3,nbrOfPlayers+1));

		panelScoreBoard.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 0, 0, 0),BorderFactory.createLineBorder(Color.BLACK,5,false)));
		panelScoreBoard.setBackground(new Color(218,242,245));


		scoreBoard =  new JLabel[nbrOfRounds*2+3][nbrOfPlayers+1];
		for(int i = 0;i<nbrOfRounds*2+3;i++) {
			for(int j=0;j<=nbrOfPlayers;j++){
				if(j==0) {
					if(i>1 && i< nbrOfRounds*2+2) {
						if(i<=nbrOfRounds+1) {
							scoreBoard[i][j] = new JLabel("  "+(nbrOfRounds+2-i));
						} else {
							scoreBoard[i][j] = new JLabel("  "+(i-nbrOfRounds-1));	
						}
					} else if(i == nbrOfRounds*2+2) {
						scoreBoard[i][j] = new JLabel("Tot score: ");

					} else {
						scoreBoard[i][j] = new JLabel("");
					}

				} else {
					if(i==0) {
						if(j == nbrOfPlayers/2) {
							scoreBoard[i][j] = new JLabel("Players");
						} else {
							scoreBoard[i][j] = new JLabel("");
						}
					}
					else if(i==1) {
						if(id==j) {
							scoreBoard[i][j] = new JLabel(""+j +"(you)    ");
						} else {
							scoreBoard[i][j] = new JLabel(""+j);
						}
					} else if(i==nbrOfRounds*2+2) {

						scoreBoard[i][j] = new JLabel("0");

					} else {
						scoreBoard[i][j] = new JLabel("");
					}
				}
				scoreBoard[i][j].setHorizontalAlignment(JLabel.CENTER);
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
		if(card == null) {
			path = "transparent.png";
			return path;
		}
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
		ImageIcon imgIcon = new ImageIcon("Resources/"+path);
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
					nextCardThread.kill();
					Thread t = new Thread() {
						public void run() {
							System.out.println("send to monitor");
							textMessage.setText("Wait for other players");
							monitor.addNextCard(card);

						}
					};
					t.start();
					currentHand.remove(card);
					myCards.remove(comp);


					myCards.revalidate();
					repaint();
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
						nextCardThread.kill();
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
		textMessage.setText("Choice next card: ");
		nextCardThread = new TimerThread(15*1000,textMessage,this);
		nextCardThread.setTextBefore(textMessage.getText());
		nextCardThread.setActionOnFinish(2);
		nextCardThread.start();
		// start timer here and put text.
		revalidate();

	}

	/**
	 * Tells player that it is its turn to choice the number of sticks.
	 */
	public void setSticks() {
		textMessage.setText("Set number of sticks: ");
		stickThread = new TimerThread(15*1000, textMessage,this);
		stickThread.setTextBefore(textMessage.getText());
		stickThread.setActionOnFinish(1);
		stickThread.start();
		revalidate();
		setSticks = true;
	}

	/**
	 * Set the score for player 
	 * @param score The score
	 * @param playerId The player
	 */
	public void setScore(int score, int playerId) {
		scoreBoard[roundNbr+1][playerId].setText(score+"");
		revalidate();

	}

	/**
	 * Update screen then finish dealing. 
	 */
	public void finishDealing() {
		Collections.sort(currentHand);

		ImageIcon icon = createCardOnHand(null);
		JLabel label = new JLabel();
		label.setIcon(icon); 
		label.setHorizontalAlignment(JLabel.CENTER);
		myCards.add(label);


		for(Card card:currentHand) {
			icon = createCardOnHand(card);
			label = new JLabel();
			label.setIcon(icon); 
			label.addMouseListener(new CardListener(card,label));
			label.setHorizontalAlignment(JLabel.CENTER);

			myCards.add(label);
		}


		revalidate();
	}

	public void auto(int action) {
		switch(action) {
		case 1:
			int sticks = (int)spinner.getValue();
			if(playersSetSticks == nbrOfPlayers-1 && sticks + totalSticks == sticksInRound) {
				if(sticks == 0) {
					sticks = 1;
				} else {
					sticks--;
				}
			}
			setSticks = false;
			System.out.println("count: "+ spinner.getValue());
			monitor.addNumberOfSticks((int)spinner.getValue());
			textMessage.setText("Wait for other players...");
			revalidate();
			break;
		case 2:
			choiceCard = false;
			for(Card card:currentHand) {
				if(nbrOfPlayedCards==nbrOfPlayers || card.getSuit()== playedSuit) {
					System.out.println("send to monitor");
					textMessage.setText("Wait for other players");
					monitor.addNextCard(card);
					currentHand.remove(card);
					// How to fix?!
					//myCards.remove(comp);

					myCards.revalidate();
					repaint();


				} else {

					boolean hasTrumf = false;
					boolean hasSuit = false;

					for(Card card2: currentHand) {
						if(card2.getSuit()==trumf.getSuit()) {
							hasTrumf = true;
						}
						if(card2.getSuit()==playedSuit) {
							hasSuit = true;
						}
					}
					if(hasSuit) {
						continue;
					} else if(hasTrumf && card.getSuit() != trumf.getSuit()) {
						continue;
					} else {
						System.out.println("send to monitor");
						textMessage.setText("Wait for other players");
						revalidate();
						monitor.addNextCard(card);

						currentHand.remove(card);
						// FIX this..
						//myCards.remove(comp);

						revalidate();
						// send card and delete from view...

					}



				}

			}



		}

	}



}
