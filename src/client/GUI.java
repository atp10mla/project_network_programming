package client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class GUI extends JFrame{
	public GUI() {
		setTitle("Plump");
		setSize(300,200); // default size is 0,0
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout(100,100));

		
		InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream("Resources/4_of_clubs.png");
				try {
					BufferedImage image = ImageIO.read(in);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		panel.add(null,BorderLayout.CENTER);
		panel.add(null,BorderLayout.CENTER);
		panel.add(new JLabel("Example"), BorderLayout.EAST);
	}
	
}
