package protocol;

public class Card {
	public static final int SPADES = 1;
	public static final int HEARTS = 2;
	public static final int DIAMONDS = 3;
	public static final int CLUBS = 4;
	
	public static final int ACE = 1;
	public static final int JACK = 11;
	public static final int QUEEN = 12;
	public static final int KING = 13;
	
	private int value;
	private int suit;
	
	public Card(int value, int suit) {
		this.value = value;
		this.suit = suit;
	}

	public int getValue() {
		return value;
	}

	public int getSuit() {
		return suit;
	}
	
}
