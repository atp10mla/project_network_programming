package server;

import java.io.IOException;
import java.io.InputStream;

public class InputHandler extends Thread {

	private InputStream is;
	private Monitor monitor;

	public InputHandler(InputStream is, Monitor monitor) {
		this.is = is;
		this.monitor = monitor;
	}
	
	public void run() {
		while(true) {
			int com = 0;
			try {
				com = is.read();
			} catch (IOException e) {
				System.out.println("Could not read from is");
				System.exit(1);
			}
			monitor.executeCommando(com);
			
		}
		
	}

}
