package server;

public class StartGun extends Thread {
	Monitor monitor;
	
	public StartGun(Monitor m) {
		monitor = m;
	}
		
	public void run() {
		monitor.waitForStart();
	}
	
}
