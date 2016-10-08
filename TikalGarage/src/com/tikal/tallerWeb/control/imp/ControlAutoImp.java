package com.tikal.tallerWeb.control.imp;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.tikal.tallerWeb.control.ControlAuto;
import com.tikal.tallerWeb.data.access.AutoDAO;

import technology.tikal.taller.automotriz.model.index.servicio.ServicioIndexAutoData;

public class ControlAutoImp implements ControlAuto {

	@Autowired
	AutoDAO autoDAO;

	@Override
	@RequestMapping(value={"/loadAuto"},method= RequestMethod.GET)	
	public void loadAuto(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response,
			ServicioIndexAutoData origen) {
		this.autoDAO.cargar(origen.getNumeroSerie());
	}

	@Override
	@RequestMapping(value={"/getAutos"},method= RequestMethod.GET)
	public List<ServicioIndexAutoData> getAutos(javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response) {
		this.autoDAO.getIndiceAutos();
		return null;
	}

	@Override
	public void buscarAuto(javax.servlet.http.HttpServletRequest request,
			javax.servlet.http.HttpServletResponse response, String numeroSerie, List<ServicioIndexAutoData> cmd) {
		this.autoDAO.buscar(numeroSerie, cmd);
		//Cambiar a String		
	}


}