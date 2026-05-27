package com.farmacia.erp.controlador;

import com.farmacia.erp.entidades.OrdenDesechos;
import com.farmacia.erp.repositorio.OrdenDesechoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
        import java.util.List;

@RestController
@RequestMapping("/api/desechos")
public class OrdenDesechoControlador {

    @Autowired
    private OrdenDesechoRepositorio repository;

    @GetMapping
    public List<OrdenDesechos> obtenerTodos() {
        return repository.findAll();
    }

    @PostMapping
    public OrdenDesechos crearDesecho(@RequestBody OrdenDesechos desecho) {
        return repository.save(desecho);
    }

    @PutMapping("/{id}")
    public OrdenDesechos actualizarDesecho(@PathVariable String id, @RequestBody OrdenDesechos detalles) {
        return repository.findById(id)
                .map(existente -> {
                    existente.setIdMedicamento(detalles.getIdMedicamento());
                    existente.setCantidadPerdida(detalles.getCantidadPerdida());
                    existente.setFechaBaja(detalles.getFechaBaja());
                    return repository.save(existente);
                })
                .orElseThrow(() -> new RuntimeException("No encontrado"));
    }

    @DeleteMapping("/{id}")
    public String borrarDesecho(@PathVariable String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return "Eliminado";
        }
        return "No encontrado";
    }
}