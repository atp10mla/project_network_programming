package protocol;

public class Protocol {

	public static final int SEND_CARD = 1; // followed by suit, value
	public static final int GET_DELAY = 2; // Ask for 2 min extra time
	
	
	public static final int YOUR_TURN = 3;
	public static final int PLAYED_CARD = 4; // followed by suit, value
	
	public static final int NEW_ROUND = 5; // followed by nbr_of_cards, suit_card1, value_card1, suit_card2, value_card2,....
	public static final int SET_TRUMF = 6; // followed by suit
	
	public static final int STICK_WINNER = 7; // followed by id (1->nbr_of_players)
	
	public static final int SET_WANTED_STICKS = 8; // followed by playerNbr and nbr_of_sticks
	
	
	
}
