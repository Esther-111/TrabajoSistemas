package com.farmacia.erp.controlador;

import com.farmacia.erp.entidades.Medicamento;
import com.farmacia.erp.dto.MedicamentoCrearDTO;
import com.farmacia.erp.servicio.MedicamentoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

        import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
@RequiredArgsConstructor
public class MedicamentoControlador {

    private final MedicamentoServicio servicio;

    @GetMapping
    public List<Medicamento> obtenerTodos() {
        return servicio.listarTodos();
    }

    @PostMapping
    public Medicamento crearMedicamento(@RequestBody MedicamentoCrearDTO dto) {
        Medicamento nuevo = new Medicamento();
        nuevo.setIdLaboratorio(dto.getIdLaboratorio());
        nuevo.setNombreComercial(dto.getNombreComercial());
        nuevo.setPrincipioActivo(dto.getPrincipioActivo());
        nuevo.setDosis(dto.getDosis());
        nuevo.setFormaFarmaceutica(dto.getFormaFarmaceutica());
        nuevo.setCantidadStock(dto.getCantidadStock());
        nuevo.setPrecio(dto.getPrecio());
        nuevo.setFechaCaducidad(dto.getFechaCaducidad());

        return servicio.guardar(nuevo);
    }

    @PutMapping("/{id}")
    public Medicamento actualizarMedicamento(@PathVariable String id, @RequestBody MedicamentoCrearDTO dto) {
        return servicio.buscarPorId(id).map(existente -> {
            existente.setIdLaboratorio(dto.getIdLaboratorio());
            existente.setNombreComercial(dto.getNombreComercial());
            existente.setPrincipioActivo(dto.getPrincipioActivo());
            existente.setDosis(dto.getDosis());
            existente.setFormaFarmaceutica(dto.getFormaFarmaceutica());
            existente.setCantidadStock(dto.getCantidadStock());
            existente.setPrecio(dto.getPrecio());
            existente.setFechaCaducidad(dto.getFechaCaducidad());
            return servicio.guardar(existente);
        }).orElseThrow(() -> new RuntimeException("Medicamento no encontrado"));
    }

    @DeleteMapping("/{id}")
    public String borrarMedicamento(@PathVariable String id) {
        servicio.eliminar(id);
        return "Medicamento eliminado";
    }
}