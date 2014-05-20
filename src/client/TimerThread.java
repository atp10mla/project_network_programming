package client;

import javax.swing.JLabel;

public class TimerThread extends Thread{
	private int action;
	private boolean kill;
	private int time;
	private JLabel label;
	private GUI gui;
	private String textBefore = "";
	public TimerThread(int time, JLabel label, GUI gui) {
		this.time = time;
		this.label = label;
		this.gui = gui;
	}

	public void run() {
		long tick = 0;
		long startTime = System.currentTimeMillis();
		long end = startTime + time;
		while(System.currentTimeMillis()<=end) {
			if(kill) {
				return;
			}
			tick+=1000;
			label.setText(textBefore + ((time - tick)/1000)+"s");
			try {
				sleep(1000-(System.currentTimeMillis()-(startTime+tick)));
			} catch (InterruptedException e) {
			}
		}
		if(gui != null && !kill) {
			gui.makeAutoChoice(action);
		}
	}
	public void kill() {
		kill = true;
	}

	public void setActionOnFinish(int action) {
		this.action = action;
	}

	public void setTextBefore(String text) {
		textBefore = text;
	}

}
