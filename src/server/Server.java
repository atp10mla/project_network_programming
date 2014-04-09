package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
	public static void main(String[]args) {
		ServerSocket server = null;
		try {
			server = new ServerSocket(5000);
		} catch (IOException e) {
			System.out.println("Could not set up server");
			System.exit(1);
		}
		
		try {
			Socket socket = server.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
