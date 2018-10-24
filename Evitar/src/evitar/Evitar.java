package evitar;

import comunicar.Comunicar;

public class Evitar {
	
	private Comunicar inbox;
	private Comunicar gestor = new Comunicar("gestor");
	
	public Evitar() {
		System.out.println("Evitar Inicializada");
		this.inbox = new Comunicar("evitar");
	}
	
	private void descodificar(String[] msg){
		switch(msg[2]){
			case "0":
				break;
			case "1":
				gestor.enviarMsg(new byte[]{4, 5}, "");
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				gestor.enviarMsg(new byte[]{4, 2, 15}, "");
			}
		}
	
	private void run() {
		for(;;) {
			try {
				Thread.sleep(250);
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