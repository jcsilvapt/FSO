package vaguear;

import comunicar.Comunicar;

public class Vaguear {

	private Comunicar inbox;
	private Comunicar gestor;

	public Vaguear() {
		System.out.println("Vaguear - Iniciado");
		inbox = new Comunicar("vaguear");
		inbox.enviarMsg(new byte[] {Comunicar.EVITAR, Comunicar.TRUE}, "");
		gestor = new Comunicar("gestor");
	}

	private void descodificar(String msg) {
		String[] campos = msg.split(";");
		
		switch (campos[1]) {
		case "13":
			inbox.setSuspend(true);
			break;
		case "14":
			inbox.setSuspend(false);
			break;
		}
		
	}
	
	private void randomMove() throws InterruptedException {
		int accao = (int) (1 + Math.random()*4);
		int move;
		int angle;
		int radius;
		int delay = 0;
		switch (accao) {
			case 1:
				move = (int) (1 + Math.random()*127);
				gestor.enviarMsg(new byte[] {Comunicar.VAGUEAR, Comunicar.AVANCAR, (byte) move}, "");
				delay = Comunicar.delay(move, false, 0);
				break;
			case 2:
				move = (int) ((1 + Math.random()*128))*-1;
				gestor.enviarMsg(new byte[] {Comunicar.VAGUEAR, Comunicar.RECUAR, (byte) move}, "");
				delay = Comunicar.delay(move * -1, false, 0);
				break;
			case 3:
				radius = (int) (1 + Math.random()*127);
				angle = (int) (1 + Math.random()*127);
				gestor.enviarMsg(new byte[] {Comunicar.VAGUEAR,  Comunicar.ESQ, (byte) radius, (byte) angle}, "");
				delay = Comunicar.delay(radius, true, angle);
				break;
			case 4:
				radius = (int) (1 + Math.random()*127);
				angle = (int) (1 + Math.random()*127);
				gestor.enviarMsg(new byte[] {Comunicar.VAGUEAR,  Comunicar.DRT, (byte) radius, (byte) angle}, "");
				delay = Comunicar.delay(radius, true, angle);
				break;
			default:
				break;
		}
		Thread.sleep(delay);
	}
	
	public void run() {
		for (;;) {
			try {
				String msg = inbox.receberMsg();
				descodificar(msg);
				randomMove();
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			inbox.enviarMsg(new byte[] {Comunicar.EVITAR, Comunicar.TRUE}, "");
		}
	}

	public static void main(String[] args) {
		Vaguear vag = new Vaguear();
		vag.run();
	}

}