package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import protocol.Protocol;

public class Server {
	
	public static void main(String[]args) {
		final long TIME_TO_CONNECT = 1000*2*60;
		ServerSocket server = null;
		Monitor monitor = new Monitor();
		int currPlayer = 1;
		
		try {
			server = new ServerSocket(5000);
		} catch (IOException e) {
			System.out.println("Could not set up server");
			System.exit(1);
		}
		
		boolean firstTime = true;
		long timeFirstPlayerConnected = 0;
		while(!server.isClosed()) {
			Socket socket = null;
			try {
				socket = server.accept();
				if(firstTime) {
					timeFirstPlayerConnected = System.currentTimeMillis();
					new StartGun(monitor).start();
					firstTime = false;
				}
			} catch (IOException e) {
				System.out.println("Error when client connecting");
				System.exit(1);
			}
			// If player took more than 2 minutes to connect then he won't be allowed in game
			if((System.currentTimeMillis()-timeFirstPlayerConnected) < TIME_TO_CONNECT) {
				try {
					Player p = new Player(socket.getOutputStream(), "funny name", currPlayer);
					new InputHandler(socket.getInputStream(), monitor, p).start();
					new OutputHandler(socket.getOutputStream(), monitor, p).start();
					monitor.addPlayer(p);
				} catch (IOException e) {
					System.out.println("Error when client connecting");
					System.exit(1);
				}
			} else {
				System.out.println(Protocol.ERROR_TIME_OUT);
				break;
			}

		}
	}
}
