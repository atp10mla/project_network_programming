package server;

public class StartGun extends Thread {
	private Monitor monitor;
	private int timeToWait;
	
	public StartGun(Monitor m, int timeToWait) {
		this.monitor = m;
		this.timeToWait = timeToWait;
	}
		
	public void run() {
		monitor.waitForStart(timeToWait);
		monitor.startGame();
		monitor.startNewRound();
		while(true) {
			monitor.waitForNewRoundReady();
			monitor.startNewRound();
		}
	}
}
