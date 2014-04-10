package protocol;

import server.Player;

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
	private Player owner;
	
	public Card(int value, int suit, Player owner) {
		this.value = value;
		this.suit = suit;
		this.owner = owner;
	}

	public Card(int value, int suit) {
		this.value = value;
		this.suit = suit;
	}
	
	public Player getOwner() {
		return owner;
	}
	
	public int getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + suit;
		result = prime * result + value;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Card other = (Card) obj;
		if (suit != other.suit)
			return false;
		if (value != other.value)
			return false;
		return true;
	}

	public int getSuit() {
		return suit;
	}

	public boolean moreValuableThan(Card curr, Card trumf, Card first) {
		if(curr.getSuit() == suit) {
			return value > curr.getValue();
		} else if(trumf.getSuit() == suit) {
			return true;
		} else if(trumf.getSuit() == curr.getSuit()) {
			return false;
		} else if(first.getSuit() == suit) {
			return true;
		} else if(first.getSuit() == curr.getSuit()) {
			return false;
		}
		return false;
	}
	
}
