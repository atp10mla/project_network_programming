package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import protocol.Protocol;

public class Server {
	
	
	public static void main(String[]args) {
		int numberOfPlayers = 2;
<<<<<<< HEAD
		Monitor monitor = new Monitor(numberOfPlayers);
=======
		int numberOfRounds = 2;
		Monitor monitor = new Monitor(numberOfPlayers, numberOfRounds);
>>>>>>> 81f72a4f886c51ec5ae14af54c5ee54b75e0598c
		
		ServerSocket server = null;
		final long TIME_TO_CONNECT = 1000*2*60; // 2min
		int currPlayer = 1;
		try {
			System.out.println("Server starting...");
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
				System.out.println(socket.getInetAddress().getHostName() + " connected.");
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
					Player p = new Player(socket.getOutputStream(), "Dummy", currPlayer++);
					
					new InputHandler(socket.getInputStream(), monitor, p).start();
					new OutputHandler(socket.getOutputStream(), monitor, p).start();
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
