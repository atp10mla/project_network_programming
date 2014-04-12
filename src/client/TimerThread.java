package client;

import javax.swing.JLabel;

public class TimerThread extends Thread{
	private long time;
	private JLabel label;
	private String textBefore = "";
	public TimerThread(long time, JLabel label) {
		this.time = time;
		this.label = label;
	}
	
	public void run() {
		long tick = 0;
		long startTime = System.currentTimeMillis();
		long end = startTime + time;
		while(System.currentTimeMillis()<end) {
			label.setText(textBefore+(time-tick)/1000);
			tick+=1000;
			try {
				sleep(1000-(System.currentTimeMillis()-(startTime+tick)));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// write to GUI...
		}
		
		
	}
	
	public void setTextBefore(String text) {
		textBefore = text;
	}
}
