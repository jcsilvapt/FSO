package evitar;

import comunicar.Comunicar;

public class Evitar {

	private Comunicar inbox;
	private Comunicar gestor = new Comunicar("gestor");

	public Evitar() {
		System.out.println("Evitar Inicializada");
		this.inbox = new Comunicar("evitar");
	}

	private void descodificar(String msg) {

		String[] campos = msg.split(";");

		switch (campos[2]) {
		case "1":
			break;
		case "2":
			gestor.enviarMsg(new byte[] { Comunicar.EVITAR, Comunicar.PARAR }, "");
			gestor.enviarMsg(new byte[] { Comunicar.EVITAR, Comunicar.RECUAR, -15 }, "");
			gestor.enviarMsg(new byte[] { Comunicar.EVITAR, Comunicar.ESQ, 0, 90 }, "");
			break;
		}
	}

	private void run() {
		for (;;) {
			gestor.enviarMsg(new byte[] { Comunicar.EVITAR, Comunicar.RTOQUE }, "");
			try {
				String msg = inbox.receberMsg();
				descodificar(msg);
				System.out.println(msg);
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