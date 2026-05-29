package com.farmacia.erp.controlador;

import com.farmacia.erp.dto.DesechosPorMesDTO;
import com.farmacia.erp.dto.GastoPorLaboratorioDTO;
import com.farmacia.erp.servicio.EstadisticasServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/estadisticas")
@RequiredArgsConstructor
public class EstadisticasControlador {

    private final EstadisticasServicio estadisticasServicio;

    // Endpoint 1: Gráfica de Gastos
    @GetMapping("/gastos")
    public List<GastoPorLaboratorioDTO> getGastosPorLaboratorio() {
        return estadisticasServicio.calcularGastosPorLaboratorio();
    }

    // Endpoint 2: Gráfica de Desechos
    @GetMapping("/desechos")
    public List<DesechosPorMesDTO> getDesechosPorMes() {
        return estadisticasServicio.calcularDesechosPorMes();
    }
}