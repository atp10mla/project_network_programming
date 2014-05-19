package protocol;

public class Protocol {

	// player sends the card he plays
	public static final int SEND_CARD = 1; // followed by suit, value
	public static final int GET_DELAY = 21; // Ask for 2 min extra time
	public static final int SEND_MORE_TIME = 22; // A player wants 2 extra min
	
	// informs player that it is his turn to play a card
	public static final int YOUR_TURN = 3;
	
	// informs player what card a certain player played
	public static final int PLAYED_CARD = 4; // followed by id, suit, value
	
	// informs player that a new game is starting
	public static final int NEW_GAME = 51; // followed by player id, number of players, number of rounds
	
	// informs player that a new round is starting
	public static final int NEW_ROUND = 5; // followed by nbr_of_cards, suit_card1, value_card1, suit_card2, value_card2,....
	
	// informs player what trumf is current, sends after NEW_ROUND
	public static final int SET_TRUMF = 6; // followed by suit
	
	// informs player whay players won a stick the played stick
	public static final int STICK_WINNER = 7; // followed by id
	
	// informs player what number of stick the person choosing wants
	public static final int SET_WANTED_STICKS = 8; // followed by playerNbr and nbr_of_sticks
	
	// informs player how much score each player got this round
	public static final int ROUND_SCORE = 9; // followed by number of players, score, score...
	
	// inform server/ask player what number of stick the person wants
	public static final int SET_STICKS = 10; // followed by nbr_of_sticks / nothing when server ask..
	
	//informs player about the winner of the game
	public static final int SET_WINNER = 11; // followed by player id
	
	public static final String ERROR_TIME_OUT = "Time to connect ended\n";
	
		
}
