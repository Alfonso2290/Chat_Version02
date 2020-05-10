package Principal.Cliente;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;
import Principal.BEAN.Usuario;

public class PanelChatCliente extends JPanel{

	private JTextArea area;
	private JScrollPane scroll;
	private JTextField texto;
	private JList<String> listaContactos;
	private DefaultListModel modeloLista;
	private JScrollPane scrollLista;
	private JLabel lblUsuario;
	private JComboBox<String> cbEstado;
	private VentanaChatCliente ventana;
	private String nombreUsuario;
	
	public PanelChatCliente(VentanaChatCliente ventana) {
		this.ventana=ventana;
		inicioComponentes();
	}

	private void inicioComponentes() {
		
		setBackground(Color.BLUE.darker());
		setLayout(null);
		Font fuenteTexto=new Font("Consolas", Font.BOLD, 15);
		
		ventana.addWindowListener(new eventoVentana());
		
		nombreUsuario=JOptionPane.showInputDialog("Ingrese nombre Usuario: ");

		lblUsuario=new JLabel("NICK:" + nombreUsuario);
		lblUsuario.setFont(fuenteTexto);
		lblUsuario.setBounds(10,15,200,25);
		lblUsuario.setForeground(Color.WHITE);
		
		cbEstado=new JComboBox<String>();
		cbEstado.addItem("Activo");
		cbEstado.addItem("Inactivo");
		cbEstado.setFont(fuenteTexto);
		cbEstado.setBounds(380,15,100,25);
		cbEstado.addActionListener(new eventoComboBox());
		
		area=new JTextArea();
		area.setEditable(false);
		area.setFont(fuenteTexto);
		scroll=new JScrollPane(area);
		scroll.setBounds(0,50,250,380);
		
		texto=new JTextField();
		texto.requestFocus();
		texto.setBounds(0,430,250,50);
		texto.setFont(fuenteTexto);
		texto.addKeyListener(new eventoEnter());
	
		modeloLista=new DefaultListModel();
		listaContactos=new JList<String>();
		scrollLista=new JScrollPane(listaContactos);
		scrollLista.setBounds(250,50,250,450);
		
		listaContactos.setVisibleRowCount(10);
		listaContactos.setModel(modeloLista);
		
		add(lblUsuario);	
		add(scroll);
		add(texto);
		add(scrollLista);
		add(cbEstado);
		
		Hilo_Lista_Contactos miHilo=new Hilo_Lista_Contactos();
		miHilo.start();
	}

	private class eventoEnter extends KeyAdapter{
		
		public void keyPressed(KeyEvent e) {
			if(e.getSource()==texto && texto.getText().length()>0) {
				
				if(e.VK_ENTER==e.getKeyCode()) {
					
					if(cbEstado.getSelectedItem().toString().equals("Activo")) {

						if(texto.getText().toString().trim().length()!=0) {
							
							if(listaContactos.isSelectionEmpty()==false) {
								
								Usuario miUsuario=null;
								try {
									Socket miSocket=new Socket("192.168.1.6",9999);
									ObjectOutputStream salida=new ObjectOutputStream(miSocket.getOutputStream());
									String ipUsuario=listaContactos.getSelectedValue();
									miUsuario=new Usuario();
									miUsuario.setNombre(nombreUsuario);
									miUsuario.setIp(ipUsuario);
									miUsuario.setEstado(cbEstado.getSelectedItem().toString());
									miUsuario.setMensaje(texto.getText());
									salida.writeObject(miUsuario);
									miSocket.close();
									
								} catch (Exception e1) {
									e1.printStackTrace();
								} 
								
								area.append("Yo: " + texto.getText() + "\n");
								texto.setText("");
							}else{
								JOptionPane.showMessageDialog(null, "Seleccione un Contacto en su lista");
							}
						}
					}else {
						JOptionPane.showMessageDialog(null, "Si se encuentra en estado Inactivo no podrá enviar mensajes");
					}
				}
			}
		}
		
	}
		
	private class Hilo_Lista_Contactos extends Thread{
		
		public void run() {
			
			try {
				ServerSocket servidor=new ServerSocket(9998);
				while(true) {
					Socket puente1=servidor.accept();
					ObjectInputStream entrada=new ObjectInputStream(puente1.getInputStream());
					Object objeto=entrada.readObject();
					ArrayList<Usuario> lista;
					Usuario usuarioRecepcion;
					
					if(objeto instanceof Usuario) {
						usuarioRecepcion=(Usuario)objeto;
						area.append( usuarioRecepcion.getNombre() + ": " + usuarioRecepcion.getMensaje() + "\n");
					}
					else {
						lista=(ArrayList<Usuario>)objeto;
						String ipActual=puente1.getLocalAddress().getHostAddress();
						modeloLista.removeAllElements();
						for(Usuario usu: lista) {
							if(usu.getIp().equals(ipActual)==false)
								modeloLista.addElement(usu.getIp());
						}
					}
					

					puente1.close();
				}
					
			} catch (Exception e) {
				
				e.printStackTrace();
			}

		}
	}
	
	private void procesadoListaContactos(String estado,int señal) {
		try {
			
			Socket miSocket=new Socket("192.168.1.6",9999);
			ObjectOutputStream salida=new ObjectOutputStream(miSocket.getOutputStream());
			Usuario miUsuario=new Usuario();
			miUsuario.setEstado(estado);
			miUsuario.setSeñal(señal);
			salida.writeObject(miUsuario);
			miSocket.close();
			
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		
	}
	
	private class eventoVentana extends WindowAdapter{
		
		public void windowOpened(WindowEvent e) {
			procesadoListaContactos("Activo",1);
		}
		
		public void windowClosing(WindowEvent e) {
			procesadoListaContactos("Inactivo",1);
		}
	}
	
	private class eventoComboBox implements ActionListener{
		
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==cbEstado) {
				procesadoListaContactos(cbEstado.getSelectedItem().toString(),2);
			}
		}
	}
}
