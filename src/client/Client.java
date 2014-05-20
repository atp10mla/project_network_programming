package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {	
	
	public static void main(String[]args) {
		String server = "localhost";
		int port = 5000;
		if(args.length == 2) {
			try {
				server = args[0];
				port = Integer.parseInt(args[1]);
			} catch(Throwable e) {
				System.out.println("You start the server by typing the following: \n"
						+ "java -jar plumpClient.jar <server> <port>");	
				return;
			}
		} else if(args.length == 0) {
			// Do nothing...
		} else {
			System.out.println("You start the server by typing the following: \n"
					+ "java -jar plumpClient.jar <server> <port>");
			return;
		}

		System.out.println("You try to connect to a game on " + server +" on port "+port+".");
		
		Monitor monitor = new Monitor();
		GUI gui = new GUI(monitor);

		Socket socket = null; 
		try {
			socket = new Socket(server, port);
		} catch (UnknownHostException e) {
			System.out.println("Unknown host");
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Could not connect to server.");
			System.exit(0);
		}
		InputStream is = null;
		OutputStream os = null;
		try {
			is = socket.getInputStream();
			os = socket.getOutputStream();
		} catch (IOException e) {
			System.out.println("Failed to open output/input stream.");
			System.exit(0);
		}
		new OSHandler(os, monitor).start();
		new ISHandler(is, gui).start();
	
	}
}
