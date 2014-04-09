package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
// danne kommenterar :3 #iknoweverything #elitehaxx3r <=> SÄMST
public class Client {	
	
	public static void main(String[]args) {
		GUI gui = new GUI();
		gui.show();
		/*
		Socket socket = null; 
		try {
			socket = new Socket("localhost", 10000);
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
			System.out.println("Fail to open output/input stream.");
			System.exit(0);
		}
		Monitor monitor = new Monitor();
		new OSHandler(os, monitor).start();
		new ISHandler(is, monitor).start();
		
		
		*/
	}

}
