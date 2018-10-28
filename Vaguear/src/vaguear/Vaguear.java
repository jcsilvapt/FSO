package vaguear;

import comunicar.Comunicar;

public class Vaguear {

	private Comunicar inbox;
	private Comunicar gestor;

	public Vaguear() {
		System.out.println("Vaguear - Iniciado");
		inbox = new Comunicar("vaguear");
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
	
	private void randomMove() {
		int accao = (int) (1 + Math.random()*4);
		int move;
		int angle;
		int radius;
		switch (accao) {
		case 1:
			move = (int) (1 + Math.random()*127);
			gestor.enviarMsg(new byte[] {Comunicar.VAGUEAR, Comunicar.AVANCAR, (byte) move}, "");
			break;
		case 2:
			move = (int) ((1 + Math.random()*128))*-1;
			gestor.enviarMsg(new byte[] {Comunicar.VAGUEAR, Comunicar.RECUAR, (byte) move}, "");
			break;
		case 3:
			radius = (int) (1 + Math.random()*127);
			angle = (int) (1 + Math.random()*127);
			gestor.enviarMsg(new byte[] {Comunicar.VAGUEAR,  Comunicar.ESQ, (byte) radius, (byte) angle}, "");
			break;
		case 4:
			radius = (int) (1 + Math.random()*127);
			angle = (int) (1 + Math.random()*127);
			gestor.enviarMsg(new byte[] {Comunicar.VAGUEAR,  Comunicar.DRT, (byte) radius, (byte) angle}, "");
			break;
		}
	}
	
	public void run() {
		for (;;) {
			randomMove();
			try {
				String msg = inbox.receberMsg();
				System.out.println(msg);
				descodificar(msg);
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}

	public static void main(String[] args) {
		Vaguear vag = new Vaguear();
		vag.run();
	}

}