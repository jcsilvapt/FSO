package gestor;

import java.util.Arrays;

import comunicar.Comunicar;
import myrobot.myRobot;

public class Gestor {

	public static final byte ID = 1;

	private Comunicar inbox;
	private Comunicar gui;
	private Comunicar evitar;
	private Comunicar vaguear;

	private myRobot robot;
	
	private boolean runner;

	private static final int WAIT = 250;

	/**
	 * Constructor, Inicializa as restantes caixa automaticamente
	 */
	public Gestor() {
		inbox = new Comunicar("gestor");
		gui = new Comunicar("gui");
		evitar = new Comunicar("evitar");
		vaguear = new Comunicar("vaguear");
		this.robot = new myRobot();
	}

	@SuppressWarnings("unused")
	private void robotConnect(String name) {
		this.robot = new myRobot();
		boolean control = true;
		while (control) {
			if (robot.OpenEV3(name)) {
				gui.enviarMsg(new byte[] { Comunicar.GESTOR, Comunicar.TRUE }, "");
				control = false;
			} else {
				try {
					Thread.sleep(WAIT);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void descodificar(String msg) {

		String[] campos = msg.split(";");

		switch (campos[0]) {
		case "2":
		case "3":
			switch (campos[1]) {
			case "1":
				this.robot.Reta(Integer.parseInt(campos[2]));
				this.robot.Parar(false);
				break;
			case "2":
				this.robot.Reta(Integer.parseInt(campos[2]));
				this.robot.Parar(false);
				break;
			case "3":
				this.robot.CurvarEsquerda(Integer.parseInt(campos[2]), Integer.parseInt(campos[3]));
				this.robot.Parar(false);
				break;
			case "4":
				this.robot.CurvarDireita(Integer.parseInt(campos[2]), Integer.parseInt(campos[3]));
				this.robot.Parar(false);
				break;
			case "5":
				this.robot.Parar(true);
				if(runner)
					gui.enviarMsg(new byte[] {Comunicar.GESTOR, Comunicar.OPEN, 1}, "");
				break;
			case "6":
				if(this.robot.OpenEV3(campos[campos.length - 1])){
					gui.enviarMsg(new byte[] {Comunicar.GESTOR, Comunicar.OPEN, 2}, "");
				} else {
					gui.enviarMsg(new byte[] {Comunicar.GESTOR, Comunicar.OPEN, 1}, "");
				}
				break;
			case "7":
				this.robot.CloseEV3();
				gui.enviarMsg(new byte[] {Comunicar.GESTOR, Comunicar.CLOSE}, "");
				break;
			case "8":
				this.robot.AjustarVME(Integer.parseInt(campos[2]));
				break;
			case "9":
				this.robot.AjustarVMD(Integer.parseInt(campos[2]));
				break;
			case "10":
				this.robot.SetVelocidade(Integer.parseInt(campos[2]));
				break;
			}
			break;
		case "4":
			switch (campos[1]) {
			case "2":
				this.robot.Reta(Integer.parseInt(campos[2]));
				this.robot.Parar(false);
				nextAction();
				break;
			case "3":
				this.robot.CurvarEsquerda(Integer.parseInt(campos[2]), Integer.parseInt(campos[3]));
				this.robot.Parar(false);
				nextAction();
				break;
			case "5":
				this.robot.Parar(true);
				nextAction();
				break;
			case "12":
				envioSensorToque();
				break;
			case "13":
				vaguear.enviarMsg(new byte[] {Comunicar.GESTOR, Comunicar.SSUSP}, "");
				gui.enviarMsg(new byte[] {Comunicar.GESTOR, Comunicar.SSUSP}, "");
				nextAction();
				break;
			case "14":
				vaguear.enviarMsg(new byte[] {Comunicar.GESTOR, Comunicar.RSUSP}, "");
				gui.enviarMsg(new byte[] {Comunicar.GESTOR, Comunicar.RSUSP}, "");
				runner = false;
				break;
			}
		}
	}
	
	private void nextAction() {
		evitar.enviarMsg(new byte[] {Comunicar.GESTOR, Comunicar.TRUE}, "");
	}

	private void envioSensorToque() {
		if (this.robot.SensorToque()) {
			evitar.enviarMsg(new byte[] { Comunicar.GESTOR, Comunicar.STOQUE, Comunicar.TRUE }, "");
			runner = true;
		} else {
			evitar.enviarMsg(new byte[] { Comunicar.GESTOR, Comunicar.STOQUE, Comunicar.FALSE }, "");
		}
	}

	public void run() {
		for (;;) {
			try {
				String msg = inbox.receberMsg();
				descodificar(msg);
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		Gestor proc = new Gestor();
		proc.run();
	}

}
