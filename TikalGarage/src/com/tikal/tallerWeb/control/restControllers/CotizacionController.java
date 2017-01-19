package com.tikal.tallerWeb.control.restControllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.googlecode.objectify.Key;
import com.tikal.tallerWeb.control.restControllers.VO.CotizacionVO;
import com.tikal.tallerWeb.control.restControllers.VO.DatosServicioVO;
import com.tikal.tallerWeb.control.restControllers.VO.GruposCosto;
import com.tikal.tallerWeb.control.restControllers.VO.PiezaCotizacionVO;
import com.tikal.tallerWeb.data.access.CotizacionDAO;
import com.tikal.tallerWeb.data.access.ServicioDAO;
import com.tikal.tallerWeb.modelo.entity.CotizacionEntity;
import com.tikal.tallerWeb.modelo.entity.PresupuestoEntity;
import com.tikal.tallerWeb.modelo.entity.ServicioEntity;
import com.tikal.tallerWeb.util.AsignadorDeCharset;
import com.tikal.tallerWeb.util.JsonConvertidor;

@Controller
@RequestMapping(value = { "/cotizacion" })
public class CotizacionController {

	@Autowired
	CotizacionDAO cotizaciondao;
	@Autowired
	ServicioDAO servdao;

	@RequestMapping(value = { "/get" }, method = RequestMethod.GET, produces = "Application/Json")
	public void get(HttpServletRequest request, HttpServletResponse response)
			throws NumberFormatException, IOException {
		AsignadorDeCharset.asignar(request, response);
		String tipo = request.getParameter("tipo");
		String anio = request.getParameter("modelo");
		System.out.println("Tipo " + tipo + " Modelo " + anio);
		response.getWriter().println(JsonConvertidor.toJson(cotizaciondao.consultar(tipo, Integer.parseInt(anio))));
	}

	@RequestMapping(value = { "/getFull" }, method = RequestMethod.GET, produces = "Application/Json")
	public void getFull(HttpServletRequest request, HttpServletResponse response)
			throws NumberFormatException, IOException {
		AsignadorDeCharset.asignar(request, response);
		String cadena = request.getParameter("cadena");
		String tipo = request.getParameter("tipo");
		String anio = request.getParameter("modelo");
		CotizacionVO datosProv= (CotizacionVO) JsonConvertidor.fromJson(request.getParameter("proveedores"), CotizacionVO.class);
		Long idServicio = Long.parseLong(request.getParameter("idServicio"));

		List<CotizacionEntity> guardados = cotizaciondao.consultar(idServicio);
		List<String> todos = new ArrayList<String>();
		boolean completa = Boolean.parseBoolean(request.getParameter("full"));
		if (completa) {
			DatosServicioVO datos = (DatosServicioVO) JsonConvertidor.fromJson(cadena, DatosServicioVO.class);
			for (GruposCosto g : datos.getPresupuesto()) {
				for (PresupuestoEntity p : g.getPresupuestos()) {
					if (p.getSubtipo().compareToIgnoreCase("RE") == 0 || p.getSubtipo().compareToIgnoreCase("IN") == 0
							|| p.getSubtipo().compareToIgnoreCase("SE") == 0) {
						todos.add(p.getConcepto());
					}
				}
			}
		}

		if (!completa) {
			PresupuestoEntity grupos = (PresupuestoEntity) JsonConvertidor.fromJson(cadena, PresupuestoEntity.class);
			List<String> cotizar = new ArrayList<String>();
			todos.add(grupos.getConcepto());
			cotizar.add(grupos.getConcepto());
			// for (GruposCosto g : grupos.getPresupuesto()) {
			// for (PresupuestoEntity p : g.getPresupuestos()) {
			// if (p.getSubtipo().compareToIgnoreCase("RE") == 0 ||
			// p.getSubtipo().compareToIgnoreCase("IN") == 0) {
			// cotizar.add(p.getConcepto());
			// }
			// }
			// }
			List<CotizacionEntity> cots = cotizaciondao.consultarFull(tipo, Integer.parseInt(anio), cotizar);
			for (CotizacionEntity cot : cots) {
				cot.setSelected(false);
				cot.setFecha(new Date());
			}
			boolean flag = true;
			for (int i = 0; i < guardados.size(); i++) {
				CotizacionEntity cot = guardados.get(i);
				if (cot.getConcepto().compareTo(grupos.getConcepto()) == 0) {
					guardados.set(i, cots.get(0));
					flag = false;
					break;
				}
			}
			if (flag) {
				guardados.addAll(cots);
			}
		}
		Map<String, List<CotizacionEntity>> mapa = new HashMap<String, List<CotizacionEntity>>();
		List<String> proveedores = new ArrayList<String>();
		for(String provee:datosProv.getProveedores()){
			List<CotizacionEntity> precios = new ArrayList<CotizacionEntity>();
			mapa.put(provee, precios);
			proveedores.add(provee);
		}
		for (CotizacionEntity cot : guardados) {
			if (mapa.containsKey(cot.getProveedor())) {
				mapa.get(cot.getProveedor()).add(cot);

			} else {
				List<CotizacionEntity> precios = new ArrayList<CotizacionEntity>();
				precios.add(cot);
				mapa.put(cot.getProveedor(), precios);
				proveedores.add(cot.getProveedor());
			}
		}
		CotizacionVO ret = new CotizacionVO();
		// for (Map.Entry<String, List<CotizacionEntity>> entry :
		// mapa.entrySet()) {
		// System.out.println(entry.getKey() + "/" + entry.getValue());
		// }

		List<PiezaCotizacionVO> lista = new ArrayList<PiezaCotizacionVO>();
		for (int j = 0; j < todos.size(); j++) {
			PiezaCotizacionVO pcvo = new PiezaCotizacionVO();
			pcvo.setConcepto(todos.get(j));

			for (int i = 0; i < proveedores.size(); i++) {
				boolean nohay = true;
				for (CotizacionEntity cot : mapa.get(proveedores.get(i))) {
					if (cot.getConcepto().compareToIgnoreCase(pcvo.getConcepto()) == 0) {
						pcvo.getCostos().add(cot);
						nohay = false;
					}
				}
				if (nohay) {
					pcvo.getCostos().add(new CotizacionEntity());
				}
			}
			lista.add(pcvo);
		}
		ret.setProveedores(proveedores);
		ret.setCostos(lista);
		response.getWriter().println(JsonConvertidor.toJson(ret));
		// System.out.println("Tipo "+ tipo+" Modelo "+anio);
		// response.getWriter().println(JsonConvertidor.toJson(cotizaciondao.consultar(tipo,
		// Integer.parseInt(anio))));
	}

	@RequestMapping(value = { "/save" }, method = RequestMethod.POST, consumes = "Application/Json")
	public void save(HttpServletRequest request, HttpServletResponse response, @RequestBody String json)
			throws NumberFormatException, IOException {
		AsignadorDeCharset.asignar(request, response);
		CotizacionVO lista = (CotizacionVO) JsonConvertidor.fromJson(json, CotizacionVO.class);
		List<CotizacionEntity> guardar = new ArrayList<CotizacionEntity>();
		// for (CotizacionEntity o : lista.getListcotizaciones()) {
		// if (o.getId() == null) {
		// o.setModelo(Integer.parseInt(lista.getModelo()));
		// o.setTipo(lista.getTipo());
		// }
		// if (o.guardable()) {
		// guardar.add(o);
		// }
		// }
		// cotizaciondao.guarda(guardar);
		List<PiezaCotizacionVO> costos = lista.getCostos();
		for (PiezaCotizacionVO pieza : costos) {
			String concepto = pieza.getConcepto();

			for (int i = 0; i < lista.getProveedores().size(); i++) {
				if (pieza.getCostos().size() > i) {
					CotizacionEntity cot = pieza.getCostos().get(i);
					cot.setProveedor(lista.getProveedores().get(i));
					if (cot.getServicio() == null) {
						cot.setServicio(Long.parseLong(lista.getIdServicio()));
					}
					if (cot.getServicio().compareTo(Long.parseLong(lista.getIdServicio())) != 0) {
						cot.setId(null);
					}
					if (cot.getId() == null) {
						cot.setConcepto(concepto);
						cot.setModelo(Integer.parseInt(lista.getModelo()));
						cot.setTipo(lista.getTipo());
						cot.setServicio(Long.parseLong(lista.getIdServicio()));
					}
					if (cot.getPrecio() != null) {
						if (cot.getPrecio().compareTo("") != 0) {
							guardar.add(cot);
						}
					}
				}
			}
		}
		cotizaciondao.guarda(guardar);
	}

}
