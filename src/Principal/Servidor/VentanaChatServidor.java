package Principal.Servidor;

import javax.swing.*;

public class VentanaChatServidor extends JFrame {

	private PanelChatServidor lamina;
	
	public VentanaChatServidor() {
		inicioComponentes();
	}

	private void inicioComponentes() {
		setTitle("Servidor");
		setSize(500,500);
		setLocationRelativeTo(null);
		setResizable(false);
		
		lamina=new PanelChatServidor();
		add(lamina);
	}
}
