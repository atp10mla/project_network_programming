package server;

public class startGun extends Thread {
	Monitor monitor;
	
	public startGun(Monitor m) {
		monitor = m;
	}
	
	
	public void run() {
		monitor.waitForStart();
	}
	
}
