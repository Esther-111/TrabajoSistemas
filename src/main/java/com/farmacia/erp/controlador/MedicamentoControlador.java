package com.farmacia.erp.controlador;

import com.farmacia.erp.entidades.Medicamento;
import com.farmacia.erp.repositorio.MedicamentoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
        import java.util.List;

@RestController
@RequestMapping("/api/medicamentos")
public class MedicamentoControlador {

    @Autowired
    private MedicamentoRepositorio repository;

    @GetMapping
    public List<Medicamento> obtenerTodos() {
        return repository.findAll();
    }

    @PostMapping
    public Medicamento crearMedicamento(@RequestBody Medicamento medicamento) {
        return repository.save(medicamento);
    }

    @PutMapping("/{id}")
    public Medicamento actualizarMedicamento(@PathVariable String id, @RequestBody Medicamento detalles) {
        return repository.findById(id)
                .map(existente -> {
                    existente.setIdLaboratorio(detalles.getIdLaboratorio());
                    existente.setNombreComercial(detalles.getNombreComercial());
                    existente.setPrincipioActivo(detalles.getPrincipioActivo());
                    existente.setDosis(detalles.getDosis());
                    existente.setFormaFarmaceutica(detalles.getFormaFarmaceutica());
                    existente.setCantidadStock(detalles.getCantidadStock());
                    existente.setPrecio(detalles.getPrecio());
                    existente.setFechaCaducidad(detalles.getFechaCaducidad());
                    return repository.save(existente);
                })
                .orElseThrow(() -> new RuntimeException("No encontrado"));
    }

    @DeleteMapping("/{id}")
    public String borrarMedicamento(@PathVariable String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return "Eliminado";
        }
        return "No encontrado";
    }
}