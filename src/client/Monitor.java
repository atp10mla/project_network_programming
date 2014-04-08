package client;

import java.util.LinkedList;

import protocol.Card;

public class Monitor {
	private LinkedList<Integer> commandos = new LinkedList<Integer>();
	private boolean choiceNewCard = true;
	private Card nextCard;
	
	public synchronized int getNextCommando() {
		while(commandos.size() != 0) {
			try {
				wait();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
		int cmd = commandos.pop();
		notifyAll();
		return cmd;
	}
	
	

	public synchronized Card getNextCard() {		
		Card card = nextCard;
		nextCard = null;
		notifyAll();
		return card;
	}
	
	public synchronized void choiceNextCard() {		
		choiceNewCard = true;
		notifyAll();
	}
	
}
