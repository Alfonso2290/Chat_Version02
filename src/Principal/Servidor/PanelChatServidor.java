package Principal.Servidor;

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import Principal.BEAN.Usuario;
import java.util.*;

public class PanelChatServidor extends JPanel{

	private JTextArea area;
	private JScrollPane scroll;
	private ArrayList<Usuario> listaUsuarios;
	//Esta lista almacena los usuarios a los que se le enviará la lista auqnue esten inactivos
	private ArrayList<Usuario> listaUsuariosTemporal;
	
	public PanelChatServidor() {
		inicioComponentes();
	}

	private void inicioComponentes() {
		
		setLayout(new BorderLayout());
		listaUsuarios=new ArrayList<Usuario>();
		listaUsuariosTemporal=new ArrayList<Usuario>();
		
		area=new JTextArea();
		area.setFont(new Font("Consolas",Font.BOLD,14));
		area.setEditable(false);
		scroll=new JScrollPane(area);
		
		add(scroll,BorderLayout.CENTER);
		
		HiloEjecucion miHilo=new HiloEjecucion();
		miHilo.start();
	}
	
	private class HiloEjecucion extends Thread{
		
		public void run() {
			try {
				
				ServerSocket servidor=new ServerSocket(9999);
				while(true) {
					Socket miSocket=servidor.accept();
					ObjectInputStream ingreso=new ObjectInputStream(miSocket.getInputStream());
					Usuario miUsuario=(Usuario) ingreso.readObject();
					
					/*
					 SIEMPRE SEPARAR QUE TAREAS REALIZARÁ POR CADA ACCIÓN
					 EJM: EL LLENADO DE LISTA DE USUARIOS SOLO SE DA CUANDO SE CONECTAN O DESCONECTAN A LA APP
					 LOS FLAGS COMO EL CAMPO SEÑAL ES VITAL EN ESTE PUNTO 
					*/
					//(CUANDO DOY ENTER EN CHAT EVENTO: KEYLISTENER)
					if(miUsuario.getSeñal()==0) { 
					area.append("\n De " + miUsuario.getNombre() + ": " + 
							miUsuario.getMensaje() + " para " + miUsuario.getIp());
					}
					//LLENADO DE LISTA DE USUARIOS (CUANDO ENTRAN Y SALEN DEL PANEL EVENTO: WINDOWLISTENER)
					if(miUsuario.getSeñal()==1 || miUsuario.getSeñal()==2) {
						if(miUsuario.getEstado().equals("Activo")) {
							String ipConectada=miSocket.getInetAddress().getHostAddress();
							Usuario nuevoUsuario=new Usuario();
							nuevoUsuario.setIp(ipConectada);
							
							if(verificarDuplicidadLista(nuevoUsuario,listaUsuarios)==false) 
								listaUsuarios.add(nuevoUsuario);
							if(verificarDuplicidadLista(nuevoUsuario,listaUsuariosTemporal)==false)
								listaUsuariosTemporal.add(nuevoUsuario);
							
							if(miUsuario.getSeñal()==1)
								enviarSocket(listaUsuarios, listaUsuarios);
							if(miUsuario.getSeñal()==2)
								enviarSocket(listaUsuariosTemporal, listaUsuarios);
							
						}else if(miUsuario.getEstado().equals("Inactivo")) {
							
							String ipConectada=miSocket.getInetAddress().getHostAddress();
							Usuario nuevoUsuario=new Usuario();
							nuevoUsuario.setIp(ipConectada);
							
							eliminarElementoLista(listaUsuarios, ipConectada);
							if(miUsuario.getSeñal()==1)
								eliminarElementoLista(listaUsuariosTemporal, ipConectada);
							
							if(miUsuario.getSeñal()==1)
								enviarSocket(listaUsuarios, listaUsuarios);
							if(miUsuario.getSeñal()==2)
								enviarSocket(listaUsuariosTemporal, listaUsuarios);
						}
					}
					//REDIRECCION DE INFORMACION (CUANDO DOY ENTER EN CHAT)
					if(miUsuario.getSeñal()==0) {
						if(miUsuario.getEstado().equals("Activo")) {
							Socket puente2=new Socket(miUsuario.getIp(),9998);
							ObjectOutputStream salida=new ObjectOutputStream(puente2.getOutputStream());
							salida.writeObject(miUsuario);
							puente2.close();
						}
					}
					
					miSocket.close();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean verificarDuplicidadLista(Usuario usuario,ArrayList<Usuario> lista) {
		
		boolean b=false;
		for(Usuario user:lista) {
			if(usuario.getIp().equals(user.getIp()))
				b=true;
		}
		
		return b;
	}
	
	private void eliminarElementoLista(ArrayList<Usuario> lista,String ip) {
		for(int i=0;i<lista.size();i++) {
			if(lista.get(i).getIp().equals(ip))
				lista.remove(i);
		}
	}
	
	private void enviarSocket(ArrayList<Usuario> listaRecorrer,ArrayList<Usuario> listaEnviar) throws Exception{
		for(Usuario usu:listaRecorrer) {
			Socket puente1=new Socket(usu.getIp(),9998);
			ObjectOutputStream salida=new ObjectOutputStream(puente1.getOutputStream());
			salida.writeObject(listaEnviar);
			puente1.close();
		}
	}
}
