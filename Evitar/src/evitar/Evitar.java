package evitar;

public class Evitar {
	public Evitar() {
		System.out.println("I'm Evitar Class");
	}
	
	private void run() {
		for(;;) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		Evitar ev = new Evitar();
		ev.run();
	}
}