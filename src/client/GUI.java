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

public class GUI extends JFrame {
	private static final long serialVersionUID = 1L;

	private TimerThread waitThread;
	private TimerThread stickThread;
	private TimerThread nextCardThread;

	private Color backgroundGreen = new Color(14, 171, 9);

	private JLabel waitingForPlayersLabel;
	// Message element (wait for next player, set sticks...)
	private JLabel textMessage = new JLabel("Wait for other players");
	
	// Current cards on hand
	private ArrayList<Card> currentHand = new ArrayList<Card>();

	private int nbrOfPlayers;
	
	// True if your turn to set sticks.
	private boolean setSticks = false;
	// True if it is your turn to choose a card.
	private boolean chooseCard = false;
	// Vector with JLabels with sticks taken this round for each player.
	private JLabel takenSticks[];

	// Nbr of rounds. 
	private int numberOfRounds;
	private int sticksInRound; 
	
	// Time a player has to make a move, seconds
	private final int WAIT_TIME = 60;
	private final int CONNECTION_TIME = 60*1000*2;

	// JPanels
	private JPanel myCardsPanel;
	private JPanel middleCards;
	private JPanel trumfPanel;

	// Score board
	private JLabel[][] scoreBoard;

	// The monitor
	private Monitor monitor;

	// The suit of the first played card
	private int firstCardsSuit;

	// Nbr of played cards in a stick
	private int nbrOfPlayedCards;

	// Trumf card for the round
	private Card trumf;
	// Goes from 1 to 6
	private int roundNbr;

	// For stick selection
	private JSpinner spinner;
	private JButton sendSticks;

	private int nbrOfPlayersThatSetSticks;

	private int totalSticks;

	private int dir = -1;

	private static final int ACTION_SET_STICKS = 0;
	private static final int ACTION_CHOOSE_CARD = 1;

	/**
	 * 
	 * @param monitor
	 */
	public GUI(Monitor monitor) {
		setTitle("Plump");
		setSize(800, 600); // default size is 0,0
		setLayout(new BorderLayout());
		this.monitor = monitor;
		waitingForPlayersLabel = new JLabel("Waiting for players... ");

		waitingForPlayersLabel.setHorizontalAlignment(SwingConstants.CENTER);
		waitingForPlayersLabel.setVerticalAlignment(SwingConstants.CENTER);
		waitingForPlayersLabel.setFont(new Font(waitingForPlayersLabel.getFont().getFontName(),
				Font.PLAIN, 30));
		getContentPane().setBackground(backgroundGreen);

		getContentPane().add(waitingForPlayersLabel);		
		waitThread = new TimerThread(CONNECTION_TIME, waitingForPlayersLabel,(GUI)null); 
		waitThread.setTextBefore(waitingForPlayersLabel.getText());
		waitThread.start();
		
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	/**
	 * Set the trumf card for the current round.
	 * 
	 * @param card
	 *            The trumf card
	 */
	public void setTrumf(Card card) {
		this.trumf = card;
		trumfPanel.removeAll();
		ImageIcon icon = createTrumfCard(card);
		JLabel label = new JLabel();
		label.setIcon(icon);
		trumfPanel.add(new JLabel("Trumf"));
		trumfPanel.add(label);
		revalidate();
	}

	/**
	 * Add one to the stick winner.
	 * 
	 * @param playerId
	 *            id of stick winner.
	 */
	public void addStick(int playerId) {
		System.out.println("Player: " + playerId + " get one stick");
		takenSticks[playerId - 1].setText(""
				+ (Integer.parseInt(takenSticks[playerId - 1].getText()) + 1));
		middleCards.removeAll();
		middleCards.revalidate();
		repaint();
	}

	/**
	 * Set wanted sticks for a player.
	 * 
	 * @param playerId
	 *            The player
	 * @param sticks
	 *            Number of sticks
	 */
	public void setWantedSticks(int playerId, int sticks) {
		totalSticks += sticks;
		nbrOfPlayersThatSetSticks++;
		System.out.println("Player: " + playerId + " wants: " + sticks);
		scoreBoard[roundNbr][playerId].setText(sticks + "");
		revalidate();
	}

	/**
	 * Add a card to the hand.
	 * 
	 * @param card
	 *            The card
	 */
	public void addCardToHand(Card card) {
		currentHand.add(card);
	}

	/**
	 * Add next played card in the middle
	 * 
	 * @param card
	 *            The card
	 * @param player
	 *            The player who played the card.
	 */
	public void addNextPlayedCard(Card card, int player) {

		if (nbrOfPlayedCards == nbrOfPlayers) {
			middleCards.removeAll();
			nbrOfPlayedCards = 0;
			firstCardsSuit = card.getSuit();
		}
		nbrOfPlayedCards++;
		ImageIcon icon = createCardInMiddle(card);
		JLabel label = new JLabel();
		label.setIcon(icon);

		label.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
		middleCards.add(label);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setVerticalAlignment(JLabel.CENTER);
		middleCards.revalidate();
		repaint();
	}

	/**
	 * Clean the hand. New round starts.
	 */
	public void cleanHand() {
		if (dir == -1) {
			if (sticksInRound != 1) {
				sticksInRound--;
			} else {
				dir = 1;
			}
		} else {
			sticksInRound++;
		}

		totalSticks = 0;
		nbrOfPlayersThatSetSticks = 0;
		for (int i = 0; i < takenSticks.length; i++) {
			takenSticks[i].setText("0");
		}
		roundNbr++;
		currentHand.clear();
		myCardsPanel.removeAll();
		
		ImageIcon icon = createCardOnHand(null);
		JLabel label = new JLabel();
		label.setIcon(icon); 
		label.setHorizontalAlignment(JLabel.CENTER);
		myCardsPanel.add(label);
		revalidate();
	}

	/**
	 * Start a new game with a given number of players.
	 * 
	 * @param id
	 *            The id you have.
	 * @param nbrOfPlayers
	 *            Number of players
	 */
	public void newGame(int id, final int nbrOfPlayers, int numberOfRounds) {
		if(waitThread.isAlive()) {
			waitThread.kill();
		}
		this.numberOfRounds = numberOfRounds;
		this.sticksInRound = numberOfRounds + 1;

		this.nbrOfPlayers = nbrOfPlayers;
		createScoreBoard(id);
		createUpperLayout();
		trumfPanel = new JPanel();
		trumfPanel.setLayout(new BoxLayout(trumfPanel, BoxLayout.Y_AXIS));
		trumfPanel.setBackground(backgroundGreen);
		trumfPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

		roundNbr = 1;
		myCardsPanel = new JPanel();
		myCardsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
		myCardsPanel.setBackground(backgroundGreen);
		myCardsPanel.setLayout(new FlowLayout());
		middleCards = new JPanel();
		middleCards.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5,
				false));
		middleCards.setLayout(new GridBagLayout());
		middleCards.setAlignmentX(CENTER_ALIGNMENT);
		middleCards.setAlignmentY(CENTER_ALIGNMENT);
		middleCards.setBackground(new Color(214, 193, 75));
		getContentPane().add(middleCards, BorderLayout.CENTER);
		getContentPane().add(myCardsPanel, BorderLayout.SOUTH);
		getContentPane().add(trumfPanel, BorderLayout.WEST);
		nbrOfPlayedCards = nbrOfPlayers;
		
		ImageIcon icon = createTrumfCard(null);
		JLabel label = new JLabel();
		label.setIcon(icon); 
		trumfPanel.add(new JLabel("Trumf"));
		trumfPanel.add(label);
				
		this.remove(waitingForPlayersLabel);
		revalidate();
	}

	private void createUpperLayout() {
		JPanel upper = new JPanel();

		upper.setSize(100, 50);
		upper.setLayout(new BoxLayout(upper, BoxLayout.Y_AXIS));

		JPanel sticks = new JPanel();
		sendSticks = new JButton("Send sticks");
		sendSticks.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				if (setSticks) {
					if (nbrOfPlayersThatSetSticks == nbrOfPlayers - 1
							&& (int) spinner.getValue() + totalSticks == sticksInRound) {
						textMessage
								.setText("This number of sticks is not allowed!");
						revalidate();
						return;
					}
					Thread t = new Thread() {
						public void run() {
							if (stickThread.isAlive()) {
								stickThread.kill();
							}
							setSticks = false;
							System.out.println("count: " + spinner.getValue());
							monitor.addNumberOfSticksCommand((int) spinner
									.getValue());
							textMessage.setText("Wait for other players...");
							revalidate();
						}
					};
					t.start();
				}
			}
		});
		spinner = new JSpinner( new SpinnerNumberModel( 1,0,numberOfRounds,1 ) );
		sticks.add(spinner);
		sticks.add(sendSticks);

		JPanel panelTakenSticks = new JPanel();
		panelTakenSticks.setLayout(new GridBagLayout());
		panelTakenSticks.setBorder(new EmptyBorder(10, 10, 10, 10));
		GridBagConstraints c = new GridBagConstraints();

		takenSticks = new JLabel[nbrOfPlayers];
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < nbrOfPlayers; i++) {
				if (j == 0) {
					System.out.println("add player" + i);
					c.weightx = 0.5;
					c.fill = GridBagConstraints.HORIZONTAL;
					c.gridx = i;
					c.gridy = 0;

					panelTakenSticks.add(new JLabel("Player " + (i + 1)), c);

				} else {
					takenSticks[i] = new JLabel("0");

					c.fill = GridBagConstraints.HORIZONTAL;
					c.gridx = i;
					c.gridy = 11;

					panelTakenSticks.add(takenSticks[i], c);

				}
			}
		}
		upper.setBackground(backgroundGreen);
		sticks.setBackground(backgroundGreen);
		panelTakenSticks.setBackground(backgroundGreen);
		upper.add(sticks);
		upper.add(textMessage);
		textMessage.setFont(new Font(textMessage.getFont().getFontName(),
				Font.PLAIN, 20));
		textMessage.setForeground(Color.RED);
		textMessage.setAlignmentX(CENTER_ALIGNMENT);
		textMessage.setBorder(new EmptyBorder(2, 2, 2, 2));

		upper.add(panelTakenSticks);
		getContentPane().add(upper, BorderLayout.NORTH);

	}

	private void createScoreBoard(int id) {
		JPanel panelScoreBoard = new JPanel();
		panelScoreBoard.setLayout(new GridLayout(numberOfRounds*2+3,nbrOfPlayers+1));

		panelScoreBoard.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(0, 0, 0, 0),BorderFactory.createLineBorder(Color.BLACK,5,false)));
		panelScoreBoard.setBackground(new Color(218,242,245));


		scoreBoard =  new JLabel[numberOfRounds*2+3][nbrOfPlayers+1];
		for(int i = 0;i<numberOfRounds*2+3;i++) {
			for(int j=0;j<=nbrOfPlayers;j++){
				if(j==0) {
					if(i>1 && i< numberOfRounds*2+2) {
						if(i<=numberOfRounds+1) {
							scoreBoard[i][j] = new JLabel("  "+(numberOfRounds+2-i));
						} else {
							scoreBoard[i][j] = new JLabel("  "+(i-numberOfRounds-1));	
						}
					} else if(i == numberOfRounds*2+2) {
						scoreBoard[i][j] = new JLabel("Tot score: ");

					} else {
						scoreBoard[i][j] = new JLabel("");
					}

				} else {
					if (i == 0) {
						if (j == nbrOfPlayers / 2) {
							scoreBoard[i][j] = new JLabel("Players");
						} else {
							scoreBoard[i][j] = new JLabel("");
						}
					} else if (i == 1) {
						if (id == j) {
							scoreBoard[i][j] = new JLabel("" + j + "(you)    ");
						} else {
							scoreBoard[i][j] = new JLabel("" + j);
						}
					} else if(i==numberOfRounds*2+2) {

						scoreBoard[i][j] = new JLabel("0");

					} else {
						scoreBoard[i][j] = new JLabel("");
					}
				}
				scoreBoard[i][j].setHorizontalAlignment(JLabel.CENTER);
				panelScoreBoard.add(scoreBoard[i][j]);
			}
		}
		getContentPane().add(panelScoreBoard, BorderLayout.EAST);

	}

	/**
	 * Parse card to png file.
	 * 
	 * @param card
	 *            The card to parse.
	 * @return
	 */
	private String parseCardToPngString(Card card) {
		String path = "";
		if (card == null) {
			path = "transparent.png";
			return path;
		}
		switch (card.getValue()) {
		case Card.TRANSPARENT_SMALL:
			path = "transparent_small.png";
			return path;
		case Card.JACK:
			path += "jack_of_";
			break;
		case Card.QUEEN:
			path += "queen_of_";
			break;
		case Card.KING:
			path += "king_of_";
			break;
		case Card.ACE:
			path += "ace_of_";
			break;
		default:
			path += card.getValue() + "_of_";
		}
		switch (card.getSuit()) {
		case Card.HEARTS:
			path += "hearts.png";
			break;
		case Card.CLUBS:
			path += "clubs.png";
			break;
		case Card.DIAMONDS:
			path += "diamonds.png";
			break;
		case Card.SPADES:
			path += "spades.png";
			break;
		}
		return path;
	}

	private ImageIcon createCardOnHand(Card card) {
		return createImageIcon(parseCardToPngString(card), 83, 121);
	}

	private ImageIcon createCardInMiddle(Card card) {
		return createImageIcon(parseCardToPngString(card), 83, 121);
	}

	private ImageIcon createTrumfCard(Card card) {
		return createImageIcon(parseCardToPngString(card), 60, 90);
	}

	/** Returns an ImageIcon, or null if the path was invalid. */
	private ImageIcon createImageIcon(String path, int width, int height) {
		// TODO find correct folder
		ImageIcon imgIcon = new ImageIcon("Resources/" + path);
		Image img = imgIcon.getImage();
		img = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
		return new ImageIcon(img);

	}

	/**
	 * Tells player that it is its turn to choice a card.
	 */
	public void chooseNextCard() {
		chooseCard = true;
		textMessage.setText("Choose card: ");
		nextCardThread = new TimerThread(WAIT_TIME * 1000, textMessage, this);
		nextCardThread.setTextBefore(textMessage.getText());
		nextCardThread.setActionOnFinish(ACTION_CHOOSE_CARD);
		nextCardThread.start();
		// start timer here and put text.
		revalidate();

	}

	/**
	 * Tells player that it is its turn to choice the number of sticks.
	 */
	public void setSticks() {
		textMessage.setText("Set number of sticks: ");
		stickThread = new TimerThread(WAIT_TIME * 1000, textMessage, this);
		stickThread.setTextBefore(textMessage.getText());
		stickThread.setActionOnFinish(ACTION_SET_STICKS);
		stickThread.start();
		revalidate();
		setSticks = true;
	}

	/**
	 * Set the score for player
	 * 
	 * @param score
	 *            The score
	 * @param playerId
	 *            The player
	 */
	public void setRoundScore(int score, int playerId) {
		scoreBoard[roundNbr][playerId].setText(score + "");
		revalidate();
	}

	public void setTotalScore(int score, int playerId) {
		scoreBoard[numberOfRounds*2+2][playerId].setText(score+"");
		revalidate();
	}

	/**
	 * Update screen then finish dealing.
	 */
	public void createIconsForCardsOnHand() {
		Collections.sort(currentHand);

		ImageIcon icon;
		JLabel label;

		for(Card card:currentHand) {
			icon = createCardOnHand(card);
			label = new JLabel();
			label.setIcon(icon);
			label.addMouseListener(new CardListener(card, label));
			label.setHorizontalAlignment(JLabel.CENTER);

			myCardsPanel.add(label);
		}

		revalidate();
	}

	public void setWinner(int id) {
		if (id == -1) {
			textMessage.setText("The game has ended in a draw.");
		} else {
			textMessage.setText("Player " + id
					+ " has won the game. Congratulations!");
		}
	}

	public void makeAutoChoice(int action) {
		switch (action) {
		case ACTION_SET_STICKS:
			int sticks = (int) spinner.getValue();
			if (nbrOfPlayersThatSetSticks == nbrOfPlayers - 1
					&& sticks + totalSticks == sticksInRound) {
				if (sticks == 0) {
					sticks = 1;
				} else {
					sticks--;
				}
			}
			setSticks = false;
			System.out.println("count: " + spinner.getValue());
			monitor.addNumberOfSticksCommand((int) spinner.getValue());
			textMessage.setText("Waiting for other players...");
			revalidate();
			break;
		case ACTION_CHOOSE_CARD:
			chooseCard = false;
			for (Card card : currentHand) {
				if (nbrOfPlayedCards == nbrOfPlayers
						|| card.getSuit() == firstCardsSuit) {
					System.out.println("send to monitor");
					textMessage.setText("Waiting for other players...");
					monitor.setChosenCard(card);
					monitor.addSendCardCommand();
					currentHand.remove(card);
					// How to fix?!
					// myCards.remove(comp);
	
					myCardsPanel.revalidate();
					repaint();
	
				} else {
	
					boolean hasTrumf = false;
					boolean hasSuit = false;
	
					for (Card card2 : currentHand) {
						if (card2.getSuit() == trumf.getSuit()) {
							hasTrumf = true;
						}
						if (card2.getSuit() == firstCardsSuit) {
							hasSuit = true;
						}
					}
					if (hasSuit) {
						continue;
					} else if (hasTrumf && card.getSuit() != trumf.getSuit()) {
						continue;
					} else {
						System.out.println("send to monitor");
						textMessage.setText("Waiting for other players...");
						revalidate();
						monitor.setChosenCard(card);
						monitor.addSendCardCommand();
	
						currentHand.remove(card);
						// FIX this..
						// myCards.remove(comp);
	
						revalidate();
						// send card and delete from view...
	
					}
				}
			}
		}
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
			if (chooseCard) {
				boolean lastCard = nbrOfPlayedCards == nbrOfPlayers;
				if (lastCard || card.getSuit() == firstCardsSuit) {
					removeCardFromHand();
				} else {
					boolean hasTrumf = false;
					boolean hasSuit = false;
	
					for (Card card : currentHand) {
						if (card.getSuit() == trumf.getSuit()) {
							hasTrumf = true;
						}
						if (card.getSuit() == firstCardsSuit) {
							hasSuit = true;
						}
					}
					if (hasSuit) {
						return;
					} else if (hasTrumf && card.getSuit() != trumf.getSuit()) {
						return;
					} else {
						removeCardFromHand();
					}
				}
	
			}
		}
	
		private void removeCardFromHand() {
			nextCardThread.kill();
			Thread t = new Thread() {
				public void run() {
					System.out.println("send to monitor");
					textMessage.setText("Waiting for other players...");
					monitor.setChosenCard(card);
					monitor.addSendCardCommand();
	
				}
			};
			t.start();
			currentHand.remove(card);
			myCardsPanel.remove(comp);
	
			myCardsPanel.revalidate();
			repaint();
			// send card and delete from view...
			chooseCard = false;
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		@Override
		public void mousePressed(MouseEvent e) {}
		@Override
		public void mouseReleased(MouseEvent e) {}
	}
}
