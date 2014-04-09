package server;

import java.io.OutputStream;

public class OutputHandler extends Thread {
	private OutputStream os;
	private Monitor monitor;

	public OutputHandler(OutputStream os, Monitor monitor) {
		this.os = os;
		this.monitor = monitor;
	}

	public void run() {
		while(true) {
			//			try {

			monitor.startNewRound();
			
			//		} catch (IOException e) {
			//		System.out.println("Could not write from os");
			//	System.exit(1);
			//}
		}

	}

}
