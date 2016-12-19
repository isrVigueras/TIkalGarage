package com.tikal.tallerWeb.control.restControllers.VO;

import java.util.Date;

import com.tikal.tallerWeb.modelo.entity.AutoEntity;
import com.tikal.tallerWeb.modelo.entity.ClienteEntity;

import technology.tikal.taller.automotriz.model.cobranza.PagoCobranza;
import technology.tikal.taller.automotriz.model.index.servicio.ServicioIndex;
import technology.tikal.taller.automotriz.model.servicio.moneda.Moneda;

public class ServicioListVO {
	private Long id;
	private String status;
	private String nombreCliente;
	private String placas;
	private String fechaInicio;
	private String descripción;
	private String cobranza;
	private String dias;
	private String saldo;

	public ServicioListVO() {

	}

	public ServicioListVO(ServicioIndex si, AutoEntity a, ClienteEntity c) {
		this.setStatus(si.getStatus());
		this.setDescripción(si.getDescripcion());
		this.id=si.getId();
		Date fi = si.getFechaInicio();
		Date hoy = new Date();
		hoy = new Date(hoy.compareTo(fi));
		this.setDias(hoy.getDay() + "");
		this.setFechaInicio(fi.getDay() + "-" + (fi.getMonth()+1)+"-" + (900+fi.getYear()));
		this.setNombreCliente(c.getNombre());
		this.setPlacas(a.getPlacas());
		Moneda ct = si.getCostoTotal();
		if (ct != null) {
			float saldo= Float.parseFloat(ct.getValue());
			for(PagoCobranza pago:si.getCobranza().getPagos()){
				saldo= saldo - Float.parseFloat(pago.getMonto().getValue());
			}
			this.setSaldo(saldo+"");
		}
		this.setStatus(si.getStatus());
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getNombreCliente() {
		return nombreCliente;
	}

	public void setNombreCliente(String nombreCliente) {
		this.nombreCliente = nombreCliente;
	}

	public String getPlacas() {
		return placas;
	}

	public void setPlacas(String placas) {
		this.placas = placas;
	}

	public String getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(String fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public String getDescripción() {
		return descripción;
	}

	public void setDescripción(String descripción) {
		this.descripción = descripción;
	}

	public String getCobranza() {
		return cobranza;
	}

	public void setCobranza(String cobranza) {
		this.cobranza = cobranza;
	}

	public String getDias() {
		return dias;
	}

	public void setDias(String dias) {
		this.dias = dias;
	}

	public String getSaldo() {
		return saldo;
	}

	public void setSaldo(String saldo) {
		this.saldo = saldo;
	}

}