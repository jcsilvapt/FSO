package gui;

import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import comunicar.Comunicar;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class RobotGUI extends Thread {

	private JFrame frame;
	private JTextField txtNomeRobot;
	private JTextField txtDistance;
	private JTextField txtAngle;
	private JTextField txtRadius;
	private JTextField txtOffsetLeft;
	private JTextField txtOffsetRight;
	private JTextArea txtrLogging;
	private JCheckBox chckbxCheckLogging;
	private JCheckBox chckbxVaguear;
	private JCheckBox chckbxEvitar;
	private JCheckBox chckbxGestor;
	private JButton btnConectar;
	private JLabel lblConectado;

	// Variaveis Globais
	private String name;
	private byte offSetLeft, offSetRight, angle, distance, radius;
	private boolean robotOn = false;

	private Process pGestor, pVaguear, pEvitar;

	public static final byte ID = 2;

	private Comunicar inbox;
	private Comunicar gestor = new Comunicar("gestor");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		RobotGUI window = new RobotGUI();
		window.frame.setVisible(true);
		window.start();
	}

	public void run() {
		for (;;) {
			try {
				String msg = inbox.receberMsg();
				descodificar(msg);
				// System.out.println("GUI L� mensagem: " + msg);
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void descodificar(String msg) {
		String[] campos = msg.split(";");
		switch (campos[1]) {
		case "6":
			switch (campos[2]) {
			case "1":
				robotConnected(false);
				break;
			case "2":
				robotConnected(true);
				break;
			default:
				break;
			}
			break;
		case "7":
			robotConnected(false);
			break;
		case "13":
			inbox.setSuspend(true);
			break;
		case "14":
			inbox.setSuspend(false);
		}
	}

	/**
	 * Iniciar variaveis
	 */

	public void init() {
		this.inbox = new Comunicar("gui");
		this.name = ""; // default - EV3
		this.offSetLeft = 0;
		this.offSetRight = 0;
		this.angle = 90;
		this.distance = 20;
		this.radius = 10;

		this.txtNomeRobot.setText(name);
		this.txtOffsetLeft.setText(String.valueOf(offSetLeft));
		this.txtOffsetRight.setText(String.valueOf(offSetRight));
		this.txtRadius.setText(String.valueOf(radius));
		this.txtAngle.setText(String.valueOf(angle));
		this.txtDistance.setText(String.valueOf(distance));

	}

	/**
	 * Funcao para verificar textfields
	 */

	public boolean verificaCampo(String n) {
		if (n.trim().length() != 0) {
			char[] aux = n.toCharArray();
			for (int x = 0; x < aux.length; ++x) {
				if (!Character.isDigit(aux[x])) {
					return false;
				}
			}
			if (Integer.parseInt(n) > 127 || Integer.parseInt(n) < 0) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	private void BuildProcessGestor() {

		String[] arg = new String[] { "Java", "-jar", "../Gestor/Gestor.jar" };

		ProcessBuilder pbGestor = new ProcessBuilder(java.util.Arrays.asList(arg));
		pbGestor.inheritIO();
		pbGestor.redirectErrorStream(true);
		try {
			pGestor = pbGestor.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void BuildProcessEvitar() {

		String[] arg = new String[] { "Java", "-jar", "../Evitar/Evitar.jar" };

		ProcessBuilder pbEvitar = new ProcessBuilder(java.util.Arrays.asList(arg));
		pbEvitar.inheritIO();
		pbEvitar.redirectErrorStream(true);

		try {
			pEvitar = pbEvitar.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void BuildProcessVaguear() {

		String[] arg = new String[] { "Java", "-jar", "-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005" ,"../Vaguear/Vaguear.jar" };

		ProcessBuilder pbVaguear = new ProcessBuilder(java.util.Arrays.asList(arg));
		pbVaguear.inheritIO();
		pbVaguear.redirectErrorStream(true);

		try {
			pVaguear = pbVaguear.start();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Fun��o que regista o texto no logger caso este esteja activo
	 * 
	 * @param text
	 */
	public void logger(String text) {
		if (txtrLogging.isEnabled()) {
			txtrLogging.append(text + "\n");
		}
	}

	public void clearLog() {
		if (!chckbxCheckLogging.isEnabled()) {
			txtrLogging.setEnabled(true);
			txtrLogging.setText("");
			txtrLogging.setEnabled(false);
		} else {
			txtrLogging.setText("");
		}
	}

	/**
	 * Fun��o para fazer liga��o ao robot EV3
	 * 
	 * @param name
	 */
	public void robotStatus() {
		if (name.equals("") || name == null || name.length() == 0) {
			logger("Nome do Robot vazio...");
		} else {
			if (!this.robotOn)
				gestor.enviarMsg(new byte[] { Comunicar.GUI, Comunicar.OPEN }, this.name);
			else {
				gestor.enviarMsg(new byte[] { Comunicar.GUI, Comunicar.CLOSE }, "");
			}
		}

	}

	private void robotConnected(boolean value) {
		if (value) {
			btnConectar.setText("Desligar");
			lblConectado.setBackground(Color.GREEN);
			this.robotOn = true;
		} else {
			btnConectar.setText("Ligar");
			lblConectado.setBackground(Color.RED);
			this.robotOn = false;
		}
	}

	public void disconnectRobot() {
		// robot.CloseEV3();
		btnConectar.setText("Ligar");
		lblConectado.setBackground(Color.RED);
	}

	/**
	 * Create the application.
	 */
	public RobotGUI() {
		initialize();
		init();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("..:FSO-TP1:..");
		frame.setResizable(false);
		frame.getContentPane().setBackground(Color.BLACK);
		frame.setBounds(100, 100, 573, 588);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		frame.setLocationRelativeTo(null);

		JPanel panelConeccao = new JPanel();
		panelConeccao.setBorder(
				new TitledBorder(null, "CONEC\u00C7\u00C3O", TitledBorder.LEFT, TitledBorder.TOP, null, Color.WHITE));
		panelConeccao.setBackground(Color.BLACK);
		panelConeccao.setBounds(10, 10, 548, 79);
		frame.getContentPane().add(panelConeccao);
		panelConeccao.setLayout(null);

		JLabel lblNomeRobot = new JLabel("Nome");
		lblNomeRobot.setHorizontalAlignment(SwingConstants.LEFT);
		lblNomeRobot.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblNomeRobot.setForeground(Color.WHITE);
		lblNomeRobot.setBounds(10, 24, 47, 40);
		panelConeccao.add(lblNomeRobot);

		txtNomeRobot = new JTextField();
		txtNomeRobot.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyChar() == KeyEvent.VK_ENTER) {
					name = txtNomeRobot.getText();
				}
			}
		});
		txtNomeRobot.setHorizontalAlignment(SwingConstants.CENTER);
		txtNomeRobot.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtNomeRobot.setBounds(67, 34, 226, 21);
		panelConeccao.add(txtNomeRobot);
		txtNomeRobot.setColumns(10);

		btnConectar = new JButton("Ligar");
		btnConectar.setToolTipText("Ligar Gestor");
		btnConectar.setEnabled(false);
		btnConectar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (btnConectar.isEnabled()) {
					name = txtNomeRobot.getText();
					robotStatus();
				}
			}
		});
		btnConectar.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnConectar.setBounds(420, 34, 112, 19);
		panelConeccao.add(btnConectar);

		lblConectado = new JLabel("");
		lblConectado.setBackground(Color.RED);
		lblConectado.setOpaque(true);
		lblConectado.setForeground(Color.RED);
		lblConectado.setBounds(420, 55, 112, 3);
		panelConeccao.add(lblConectado);

		JPanel panelRobot = new JPanel();
		panelRobot.setBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.LOWERED, new Color(255, 255, 255), new Color(160, 160, 160)), "ROBOT",
				TitledBorder.LEFT, TitledBorder.TOP, null, new Color(255, 255, 255)));
		panelRobot.setBackground(Color.BLACK);
		panelRobot.setBounds(10, 99, 548, 214);
		frame.getContentPane().add(panelRobot);
		panelRobot.setLayout(null);

		JLabel lblDistancia = new JLabel("Dist\u00E2ncia");
		lblDistancia.setHorizontalAlignment(SwingConstants.LEFT);
		lblDistancia.setForeground(Color.WHITE);
		lblDistancia.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblDistancia.setBounds(10, 33, 60, 50);
		panelRobot.add(lblDistancia);

		JLabel lblAngulo = new JLabel("\u00C2ngulo");
		lblAngulo.setHorizontalAlignment(SwingConstants.LEFT);
		lblAngulo.setForeground(Color.WHITE);
		lblAngulo.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblAngulo.setBounds(10, 93, 60, 50);
		panelRobot.add(lblAngulo);

		JLabel lblRaio = new JLabel("Raio");
		lblRaio.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblRaio.setForeground(Color.WHITE);
		lblRaio.setBounds(10, 153, 60, 50);
		panelRobot.add(lblRaio);

		txtDistance = new JTextField();
		txtDistance.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				txtDistance.selectAll();
			}
		});
		txtDistance.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (verificaCampo(txtDistance.getText())) {
					txtDistance.setBackground(Color.white);
					distance = Byte.parseByte(txtDistance.getText());
				} else {
					txtDistance.setBackground(Color.red);
				}
			}
		});
		txtDistance.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if (event.getKeyChar() == KeyEvent.VK_ENTER) {
					if (verificaCampo(txtDistance.getText())) {
						txtDistance.setBackground(Color.white);
						distance = Byte.parseByte(txtDistance.getText());
					} else {
						txtDistance.setBackground(Color.red);
					}
				}
			}
		});
		txtDistance.setHorizontalAlignment(SwingConstants.CENTER);
		txtDistance.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtDistance.setBounds(80, 49, 76, 19);
		panelRobot.add(txtDistance);
		txtDistance.setColumns(10);

		txtAngle = new JTextField();
		txtAngle.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if(verificaCampo(txtAngle.getText())) {
					txtAngle.setBackground(Color.WHITE);
					angle = Byte.parseByte(txtRadius.getText());
				}else {
					txtRadius.setBackground(Color.RED);
				}
			}
		});
		txtAngle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				txtAngle.selectAll();
			}
		});
		txtAngle.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyChar() == KeyEvent.VK_ENTER) {
					if (verificaCampo(txtAngle.getText())) {
						txtAngle.setBackground(Color.white);
						angle = Byte.parseByte(txtAngle.getText());
					} else {
						txtAngle.setBackground(Color.red);
					}
				}
			}
		});
		txtAngle.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtAngle.setHorizontalAlignment(SwingConstants.CENTER);
		txtAngle.setBounds(80, 111, 76, 19);
		panelRobot.add(txtAngle);
		txtAngle.setColumns(10);

		txtRadius = new JTextField();
		txtRadius.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				txtRadius.selectAll();
			}
		});
		txtRadius.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if(verificaCampo(txtRadius.getText())) {
					txtRadius.setBackground(Color.WHITE);
					radius = Byte.parseByte(txtRadius.getText());
				}else {
					txtRadius.setBackground(Color.RED);
				}
			}
		});
		txtRadius.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtRadius.setHorizontalAlignment(SwingConstants.CENTER);
		txtRadius.setBounds(80, 171, 76, 19);
		panelRobot.add(txtRadius);
		txtRadius.setColumns(10);

		JButton btnAvancar = new JButton("Avan\u00E7ar");
		btnAvancar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (robotOn) {
					gestor.enviarMsg(new byte[] { Comunicar.GUI, Comunicar.AVANCAR, distance }, "");
				}

				// gestorBox.enviarMsg(new byte[] {ID,Comunicar.AVANCAR,
				// Byte.parseByte(txtDistance.getText())});
			}
		});
		btnAvancar.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnAvancar.setBounds(272, 44, 76, 32);
		panelRobot.add(btnAvancar);

		JButton btnParar = new JButton("Parar");
		btnParar.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnParar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (robotOn)
					gestor.enviarMsg(new byte[] { Comunicar.GUI, Comunicar.PARAR }, "");
			}
		});
		btnParar.setBounds(272, 104, 76, 32);
		panelRobot.add(btnParar);

		JButton btnRecuar = new JButton("Recuar");
		btnRecuar.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnRecuar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (robotOn) {
					gestor.enviarMsg(new byte[] { Comunicar.GUI, Comunicar.RECUAR, (byte) (distance*-1) }, "");
				}
				// manager.le();
				// gestorBox.enviarMsg(new byte[] {ID,Comunicar.RECUAR,
				// Byte.parseByte(txtDistance.getText())});
			}
		});
		btnRecuar.setBounds(272, 164, 76, 32);
		panelRobot.add(btnRecuar);

		JButton btnEsquerda = new JButton("Esquerda");
		btnEsquerda.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (robotOn)
					gestor.enviarMsg(new byte[] { Comunicar.GUI, Comunicar.ESQ, radius, angle }, "");
			}
		});
		btnEsquerda.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnEsquerda.setBounds(173, 104, 76, 32);
		panelRobot.add(btnEsquerda);

		JButton btnDireita = new JButton("Direita");
		btnDireita.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (robotOn)
					gestor.enviarMsg(new byte[] { Comunicar.GUI, Comunicar.DRT, radius, angle }, "");
			}
		});
		btnDireita.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnDireita.setBounds(371, 104, 76, 32);
		panelRobot.add(btnDireita);

		JLabel lblOffsetEsq = new JLabel("Offset Esquerdo");
		lblOffsetEsq.setVerticalAlignment(SwingConstants.TOP);
		lblOffsetEsq.setForeground(Color.WHITE);
		lblOffsetEsq.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblOffsetEsq.setHorizontalAlignment(SwingConstants.CENTER);
		lblOffsetEsq.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		lblOffsetEsq.setBounds(155, 20, 113, 19);
		panelRobot.add(lblOffsetEsq);

		txtOffsetLeft = new JTextField();
		txtOffsetLeft.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				offSetLeft = Byte.parseByte(txtOffsetLeft.getText());
				gestor.enviarMsg(new byte[]{Comunicar.GUI, Comunicar.VME, (byte) offSetLeft}, "");
			}
		});
		txtOffsetLeft.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				txtOffsetLeft.selectAll();
			}
		});
		txtOffsetLeft.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyChar() == KeyEvent.VK_ENTER) {
					offSetLeft = Byte.parseByte(txtOffsetLeft.getText());
					gestor.enviarMsg(new byte[]{Comunicar.GUI, Comunicar.VME, (byte) offSetLeft}, "");
				}
			}
		});
		txtOffsetLeft.setHorizontalAlignment(SwingConstants.CENTER);
		txtOffsetLeft.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtOffsetLeft.setBounds(173, 49, 76, 19);
		panelRobot.add(txtOffsetLeft);
		txtOffsetLeft.setColumns(10);

		txtOffsetRight = new JTextField();
		txtOffsetRight.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				offSetRight = Byte.parseByte(txtOffsetRight.getText());
				gestor.enviarMsg(new byte[]{Comunicar.GUI, Comunicar.VMD, (byte) offSetRight}, "");
			}
		});
		txtOffsetRight.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				txtOffsetRight.selectAll();
			}
		});
		txtOffsetRight.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent event) {
				if (event.getKeyChar() == KeyEvent.VK_ENTER) {
					offSetRight = Byte.parseByte(txtOffsetRight.getText());
					gestor.enviarMsg(new byte[]{Comunicar.GUI, Comunicar.VMD, (byte) offSetRight}, "");
				}
			}
		});
		txtOffsetRight.setHorizontalAlignment(SwingConstants.CENTER);
		txtOffsetRight.setFont(new Font("Tahoma", Font.PLAIN, 12));
		txtOffsetRight.setBounds(371, 49, 76, 19);
		panelRobot.add(txtOffsetRight);
		txtOffsetRight.setColumns(10);

		JLabel lblOffsetDrt = new JLabel("Offset Direito");
		lblOffsetDrt.setForeground(Color.WHITE);
		lblOffsetDrt.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblOffsetDrt.setHorizontalAlignment(SwingConstants.CENTER);
		lblOffsetDrt.setVerticalAlignment(SwingConstants.TOP);
		lblOffsetDrt.setBounds(359, 20, 95, 19);
		panelRobot.add(lblOffsetDrt);

		chckbxVaguear = new JCheckBox("Vaguear");
		chckbxVaguear.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!chckbxVaguear.isSelected()) {
					pVaguear.destroy();
				} else {
					BuildProcessVaguear();
				}
			}
		});
		chckbxVaguear.setEnabled(false);
		chckbxVaguear.setFont(new Font("Tahoma", Font.PLAIN, 15));
		chckbxVaguear.setForeground(Color.WHITE);
		chckbxVaguear.setBackground(Color.BLACK);
		chckbxVaguear.setBounds(461, 48, 81, 21);
		panelRobot.add(chckbxVaguear);

		chckbxEvitar = new JCheckBox("Evitar");
		chckbxEvitar.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!chckbxEvitar.isSelected()) {
					gestor.enviarMsg(new byte[] {Comunicar.EVITAR, Comunicar.RSUSP}, "");
					pEvitar.destroy();
				} else {
					BuildProcessEvitar();
				}
			}
		});
		chckbxEvitar.setEnabled(false);
		chckbxEvitar.setForeground(Color.WHITE);
		chckbxEvitar.setFont(new Font("Tahoma", Font.PLAIN, 15));
		chckbxEvitar.setBackground(Color.BLACK);
		chckbxEvitar.setBounds(461, 108, 81, 21);
		panelRobot.add(chckbxEvitar);

		chckbxGestor = new JCheckBox("Gestor");
		chckbxGestor.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (chckbxGestor.isSelected()) {
					chckbxEvitar.setEnabled(true);
					chckbxVaguear.setEnabled(true);
					btnConectar.setEnabled(true);
					btnConectar.setToolTipText(null);
					BuildProcessGestor();
				} else {
					chckbxEvitar.setEnabled(false);
					chckbxEvitar.setSelected(false);
					chckbxVaguear.setEnabled(false);
					chckbxVaguear.setSelected(false);
					btnConectar.setEnabled(false);
					btnConectar.setToolTipText("Ligar Gestor");
					if (robotOn) {
						try {
							gestor.enviarMsg(new byte[] { Comunicar.GUI, Comunicar.CLOSE }, "");
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					robotConnected(false);
					if (pEvitar != null) {
						pEvitar.destroy();
					}
					if (pVaguear != null) {
						pVaguear.destroy();
					}
					pGestor.destroy();
				}

			}
		});
		chckbxGestor.setForeground(Color.WHITE);
		chckbxGestor.setBackground(Color.BLACK);
		chckbxGestor.setFont(new Font("Tahoma", Font.PLAIN, 15));
		chckbxGestor.setBounds(461, 168, 81, 21);
		panelRobot.add(chckbxGestor);

		JPanel panelLogging = new JPanel();
		panelLogging
				.setBorder(new TitledBorder(null, "LOGGING", TitledBorder.LEFT, TitledBorder.TOP, null, Color.WHITE));
		panelLogging.setBackground(Color.BLACK);
		panelLogging.setBounds(10, 323, 548, 228);
		frame.getContentPane().add(panelLogging);
		panelLogging.setLayout(null);

		chckbxCheckLogging = new JCheckBox("Ativar Logger");
		chckbxCheckLogging.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (chckbxCheckLogging.isSelected()) {
					txtrLogging.setEnabled(true);
					chckbxCheckLogging.setText("Desactivar Logger");
				} else {
					txtrLogging.setEnabled(false);
					chckbxCheckLogging.setText("Ativar Logger");
				}
			}
		});
		chckbxCheckLogging.setSelected(true);
		chckbxCheckLogging.setFont(new Font("Tahoma", Font.PLAIN, 15));
		chckbxCheckLogging.setForeground(Color.WHITE);
		chckbxCheckLogging.setBackground(Color.BLACK);
		chckbxCheckLogging.setBounds(6, 17, 157, 27);
		panelLogging.add(chckbxCheckLogging);

		JButton btnClearLogging = new JButton("Limpar Logger");
		btnClearLogging.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clearLog();
			}
		});
		btnClearLogging.setFont(new Font("Tahoma", Font.PLAIN, 10));
		btnClearLogging.setBounds(428, 22, 110, 21);
		panelLogging.add(btnClearLogging);

		JScrollPane spLogging = new JScrollPane();
		spLogging.setFont(new Font("Tahoma", Font.PLAIN, 15));
		spLogging.setBackground(Color.BLACK);
		spLogging.setBounds(10, 50, 528, 168);
		panelLogging.add(spLogging);

		txtrLogging = new JTextArea();
		txtrLogging.setEditable(false);
		txtrLogging.setLineWrap(true);
		spLogging.setViewportView(txtrLogging);
	}

}
