package com.farmacia.erp.controlador;

import com.farmacia.erp.entidades.OrdenDesechos;
import com.farmacia.erp.dto.DesechoCrearDTO;
import com.farmacia.erp.servicio.OrdenDesechosServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/desechos")
@RequiredArgsConstructor
public class OrdenDesechoControlador {

    private final OrdenDesechosServicio servicio;

    @GetMapping
    public List<OrdenDesechos> obtenerTodos() {
        return servicio.listarTodas();
    }

    @PostMapping
    public OrdenDesechos crearDesecho(@RequestBody DesechoCrearDTO dto) {
        // Pasamos los datos del DTO al servicio para que reste el stock y genere el recibo
        return servicio.registrarDesecho(dto.getIdMedicamento(), dto.getCantidadPerdida());
    }
}