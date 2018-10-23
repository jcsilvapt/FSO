package comunicar;

public interface iMensagem {
	
	public static byte GESTOR 	= 1;
	public static byte GUI 		= 2;
	public static byte VAGUEAR 	= 3;
	public static byte EVITAR 	= 4;
	
	/* Accoes */
	public static byte AVANCAR 	= 1;
	public static byte RECUAR 	= 2;
	public static byte ESQ	    = 3;
	public static byte DRT     	= 4;
	public static byte PARAR 	= 5;
	public static byte OPEN		= 6;
	public static byte CLOSE	= 7;
	
	public static byte STOQUE	= 8;
	
	public static byte TRUE 	= 1;
	public static byte FALSE 	= 0;
	//public static byte RESPONDERTOQUE = 9;
	
	
	
	void enviarMsg(byte[] msg, String name);
	
	String receberMsg();

	//void descodificar(String msg);
	
}
