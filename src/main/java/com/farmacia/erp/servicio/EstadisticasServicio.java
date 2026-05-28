package com.farmacia.erp.servicio;

import com.farmacia.erp.dto.DesechosPorMesDTO;
import com.farmacia.erp.dto.GastoPorLaboratorioDTO;
import com.farmacia.erp.entidades.Laboratorio;
import com.farmacia.erp.entidades.OrdenDesechos;
import com.farmacia.erp.entidades.Pedido;
import com.farmacia.erp.repositorio.LaboratorioRepositorio;
import com.farmacia.erp.repositorio.OrdenDesechoRepositorio;
import com.farmacia.erp.repositorio.PedidoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstadisticasServicio {

    private final PedidoRepositorio pedidoRepositorio;
    private final OrdenDesechoRepositorio desechoRepositorio;
    private final LaboratorioRepositorio laboratorioRepositorio;

    public List<GastoPorLaboratorioDTO> calcularGastosPorLaboratorio() {
        List<Pedido> pedidos = pedidoRepositorio.findAll();
        List<Laboratorio> laboratorios = laboratorioRepositorio.findAll();

        Map<String, Double> gastosPorId = pedidos.stream()
                .collect(Collectors.groupingBy(
                        Pedido::getIdLaboratorio,
                        Collectors.summingDouble(Pedido::getCosteTotal)
                ));

        List<GastoPorLaboratorioDTO> resultado = new ArrayList<>();

        gastosPorId.forEach((idLab, total) -> {
            String nombre = laboratorios.stream()
                    .filter(l -> l.getId().equals(idLab))
                    .map(Laboratorio::getNombreEmpresa)
                    .findFirst()
                    .orElse("Laboratorio Desconocido");
            resultado.add(new GastoPorLaboratorioDTO(nombre, total));
        });

        return resultado;
    }

    public List<DesechosPorMesDTO> calcularDesechosPorMes() {
        List<OrdenDesechos> desechos = desechoRepositorio.findAll();

        Map<String, Integer> mermasPorMes = desechos.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getFechaBaja().getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES")),
                        Collectors.summingInt(OrdenDesechos::getCantidadPerdida)
                ));

        return mermasPorMes.entrySet().stream()
                .map(entry -> new DesechosPorMesDTO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }
}