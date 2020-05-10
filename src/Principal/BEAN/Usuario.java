package Principal.BEAN;

import java.io.*;

public class Usuario implements Serializable{
	
	private String ip,nombre,estado,mensaje;
	
	//EL CAMPO SEÑAL ES UN CAMPO FLAG PARA VERIFICAR ESTADO DE EVENTOS
	private int señal;

	public int getSeñal() {
		return señal;
	}

	public void setSeñal(int señal) {
		this.señal = señal;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
}
