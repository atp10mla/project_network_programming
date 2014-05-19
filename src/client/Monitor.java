package client;

import java.util.LinkedList;

import protocol.Card;
import protocol.Protocol;

public class Monitor {
	private LinkedList<Integer> commands = new LinkedList<Integer>();
	
	private Card chosenCard;
	private int nbrOfSticks;	

	public synchronized int getNextCommand() {
		while(commands.size() == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		int cmd = commands.pop();
		notifyAll();
		return cmd;
	}
	

	public synchronized Card getChosenCard() {		
		while(chosenCard == null) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		Card card = chosenCard;
		chosenCard = null;
		notifyAll();
		return card;
	}
	
	public synchronized void setChosenCard(Card card) {
		chosenCard = card;
		System.out.println(card);
		notifyAll();
	}
	
	public synchronized void addSendCardCommand() {
		commands.add(Protocol.SEND_CARD);
		notifyAll();
	}

	public synchronized int getNumberOfSticks() {
		commands.poll();
		return nbrOfSticks;
	}
	
	public synchronized void addNumberOfSticksCommand(int nbrOfSticks) {
		this.nbrOfSticks = nbrOfSticks;
		commands.addLast(Protocol.SET_STICKS);
		System.out.println("in monitor!");
		notifyAll();
	}
	
}
