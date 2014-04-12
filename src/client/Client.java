package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JFrame;
public class Client {	
	
	public static void main(String[]args) {
		Monitor monitor = new Monitor();
		GUI gui = new GUI(monitor);
		gui.setVisible(true);
		gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//gui.setDefaultCloseOperation(gui.DO_NOTHING_ON_CLOSE);
	
		Socket socket = null; 
		try {
			
			socket = new Socket("localhost", 5000);
			//socket = new Socket("localhost", 5000);
		} catch (UnknownHostException e) {
			System.out.println("Unknown host");
			System.exit(0);
		} catch (IOException e) {
			System.out.println("Could not connect to server.");
			//System.exit(0);
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
		new OSHandler(os, monitor).start();
		new ISHandler(is, gui).start();
	
	}
}
