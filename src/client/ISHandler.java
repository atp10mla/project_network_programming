package client;

import java.io.IOException;
import java.io.InputStream;

import protocol.Card;
import protocol.Protocol;

public class ISHandler extends Thread{

	private InputStream is;
	private Monitor monitor;
	public ISHandler(InputStream is, Monitor monitor) {
		this.is = is;
		this.monitor = monitor;
	}
	
	public void run() {
		try {
			while(true) {
				int cmd = is.read();
				switch(cmd) {
				case Protocol.YOUR_TURN:
					monitor.choiceNextCard();
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
		}
	
	}
	
}
