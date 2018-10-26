package evitar;

import comunicar.Comunicar;

public class Evitar {

	private Comunicar inbox;
	private Comunicar gestor = new Comunicar("gestor");
	boolean suspend = false;
	private int phase = 0;

	public Evitar() {
		System.out.println("Evitar Inicializada");
		this.inbox = new Comunicar("evitar");
	}

	private void descodificar(String msg) throws InterruptedException {

		String[] campos = msg.split(";");
		switch (campos[1]) {
		case "2":
			avoid();
			break;
		}
		if (campos.length > 2) {
			switch (campos[2]) {
			case "1":
				gestor.enviarMsg(new byte[] { Comunicar.EVITAR, Comunicar.RSUSP }, "");
				break;
			case "2":
				avoid();
				break;
			}
		}

	}

	private void avoid() throws InterruptedException {
		boolean count = true;
		if (phase == 0) {
			suspend = true;
			gestor.enviarMsg(new byte[] { Comunicar.EVITAR, Comunicar.SSUSP }, "");
			Thread.sleep(250);
		} else if (phase == 1) {
			gestor.enviarMsg(new byte[] { Comunicar.EVITAR, Comunicar.PARAR }, "");
		} else if (phase == 2) {
			gestor.enviarMsg(new byte[] { Comunicar.EVITAR, Comunicar.RECUAR, -15 }, "");
		} else if (phase == 3) {
			gestor.enviarMsg(new byte[] { Comunicar.EVITAR, Comunicar.ESQ, 0, 90 }, "");
		} else if(phase == 4) {
			gestor.enviarMsg(new byte[] { Comunicar.EVITAR, Comunicar.RSUSP }, "");
			suspend = false;
			phase = 0;
			count = false;
		}
		if (count)
			phase += 1;
	}

	private void run() {
		for (;;) {
			try {
				if (!suspend) {
					gestor.enviarMsg(new byte[] { Comunicar.EVITAR, Comunicar.RTOQUE }, "");
				}
				String msg = inbox.receberMsg();
				descodificar(msg);
				//System.out.println("Evitar Lê mensagem: " + msg);
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) {
		Evitar ev = new Evitar();
		ev.run();
	}
}