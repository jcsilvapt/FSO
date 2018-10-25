package comunicar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Arrays;

public class Comunicar implements iMensagem {

	File caixaMsg;
	FileChannel canal;
	MappedByteBuffer buffer;
	FileLock lock;
	final int BUFFER_MAX = 30;

	private boolean suspend = false;

	/* Caixas */

	public Comunicar(String nome) {
		caixaMsg = new File("..\\coms\\" + nome + ".dat");
		try {
			canal = new RandomAccessFile(caixaMsg, "rw").getChannel();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		try {
			buffer = canal.map(FileChannel.MapMode.READ_WRITE, 0, BUFFER_MAX);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isSuspend() {
		return suspend;
	}

	public void setSuspend(boolean suspend) {
		this.suspend = suspend;
	}

	public void descodificar(String msg) {
		String[] msgAux = msg.split(";");

		System.out.println(Arrays.toString(msgAux));
		switch (msgAux[0]) {
		}
	}

	public void enviarMsg(byte[] msg, String name) {
		if (!suspend) {
			lock = null;
			try {
				for (;;) {
					lock = canal.lock();
					if (buffer.get() == 0) {
						break;
					}
					lock.release();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					buffer.position(0);
				}
			} catch (IOException e) {
			} finally {
				if (lock != null) {
					try {
						lock.release();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			buffer.position(0);
			for (int x = 0; x < msg.length; ++x) {
				buffer.put(msg[x]);
			}
			buffer.put((byte) '!');
			for (int y = 0; y < name.trim().length(); ++y) {
				buffer.putChar(name.charAt(y));
			}
			buffer.putChar('\0');
		}
	}

	@Override
	public String receberMsg() {
		lock = null;
		try {
			for (;;) {
				lock = canal.lock();
				if (buffer.get() != 0) {
					break;
				}
				lock.release();
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				buffer.position(0);
			}
		} catch (IOException e) {
		} finally {
			if (lock != null) {
				try {
					lock.release();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		buffer.position(0);
		StringBuilder msg = new StringBuilder();
		char aux;
		byte var;
		boolean ptVirgula = false;
		while ((var = buffer.get()) != '!') {
			if (!ptVirgula) {
				msg.append(var);
				ptVirgula = true;
			} else {
				msg.append(";" + var);
			}
		}
		while ((aux = buffer.getChar()) != '\0') {
			if (ptVirgula) {
				msg.append(";" + String.valueOf(aux));
				ptVirgula = false;
			} else {
				msg.append(String.valueOf(aux));
			}
		}
		buffer.put(0, (byte) 0);
		return msg.toString();

	}

	public void fecharCanal() {
		try {
			canal.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
