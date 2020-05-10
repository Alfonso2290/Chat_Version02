package Principal.Cliente;

import javax.swing.*;

public class VentanaChatCliente extends JFrame{
	
	private PanelChatCliente lamina;
	
	public VentanaChatCliente() {
		inicioComponentes();
	}

	private void inicioComponentes() {
		setTitle("Cliente");
		setSize(500,500);
		setLocationRelativeTo(null);
		setResizable(false);
		
		lamina=new PanelChatCliente(this);
		add(lamina);
	}

}
