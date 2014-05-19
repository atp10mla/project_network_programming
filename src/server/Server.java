package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import protocol.Protocol;

public class Server {
	
	
	public static void main(String[]args) {
		int numberOfPlayers = 2;
		int numberOfRounds = 2;
		int port = 5000;
		if(args.length == 3) {
			try {
				port = Integer.parseInt(args[0]);
				numberOfPlayers = Integer.parseInt(args[1]);
				numberOfRounds = Integer.parseInt(args[2]);
			} catch(Throwable e) {
				System.out.println("You start the server by typing the following: \n"
						+ " java -jar plumpServer.jar <port> <number_of_players> <number_of_rounds>\n"
						+ "where:\n"
						+ "number_of_players = 2-5\n"
						+ "number_of_rounds = 2-10.");	
				return;
			}
		} else if(args.length == 0) {
			// Do nothing...
		} else {
			System.out.println("You start the server by typing the following: \n"
					+ " java -jar plumpServer.jar <port> <number_of_players> <number_of_rounds>\n"
					+ "where:\n"
					+ "number_of_players = 2-5\n"
					+ "number_of_rounds = 2-10.");
			return;
		}

		System.out.println("A game is started with up to "+numberOfPlayers+" players and "+numberOfRounds+" rounds on port "+port+".");
		Monitor monitor = new Monitor(numberOfPlayers, numberOfRounds);
		
		ServerSocket server = null;
		final long TIME_TO_CONNECT = 1000*2*60; // 2min
		int currPlayer = 1;
		try {
			System.out.println("Server starting...");
			server = new ServerSocket(port);
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
