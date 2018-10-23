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
	
	private static final int WAIT = 250;
	
	/**
	 * Constructor, Inicializa as restantes caixa automaticamente
	 */
	public Gestor() {
		inbox 		= new Comunicar("gestor");
		gui 		= new Comunicar("gui");
		evitar 		= new Comunicar("evitar");
		vaguear 	= new Comunicar("vaguear");
		this.robot 	= new myRobot();
	}
	
	public void le(){
		String msg = inbox.receberMsg();
		descodificar(msg);
		System.out.println();
		System.out.println(msg);
	}
	
	private void robotConnect(String name) {
		this.robot = new myRobot();
		boolean control = true;
		while(control) {
			if(robot.OpenEV3(name)) {
				gui.enviarMsg(new byte[] {Comunicar.GESTOR, Comunicar.TRUE}, "");
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
	
	public void descodificar(String msg){
		
		String[] campos = msg.split(";");
		
		switch(campos[0]){
			case "2": case "3":
				switch(campos[1]){
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
						break;
					case "6":
						System.out.println(Arrays.toString(campos));
						this.robot.OpenEV3(campos[campos.length-1]);
						break;
					case "7":
						this.robot.CloseEV3();
						break;
				}
				break;
			case "4":
				switch(campos[1]){
					case "8":
						break;
				}
		}
		
	}
	
	public void run() {
		for(;;) {
			try {
				String msg = inbox.receberMsg();		
				descodificar(msg);
				System.out.println(msg);
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// FIXME Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		Gestor proc = new Gestor();
		proc.run();
	}
	
}
