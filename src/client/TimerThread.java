package client;

public class TimerThread extends Thread{
	private long time;
	public TimerThread(long time) {
		this.time = time;
	}
	
	public void run() {
		long tick = 0;
		long startTime = System.currentTimeMillis();
		long end = startTime + time;
		while(System.currentTimeMillis()<end) {
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
}
