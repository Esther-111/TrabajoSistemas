package com.farmacia.erp.controlador;

import com.farmacia.erp.entidades.Laboratorio;
import com.farmacia.erp.repositorio.LaboratorioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laboratorios")
public class LaboratorioControlador {

    @Autowired
    private LaboratorioRepositorio repository;


    @GetMapping
    public List<Laboratorio> obtenerTodos() {
        return repository.findAll(); // Busca en Mongo y devuelve la lista
    }


    @PostMapping
    public Laboratorio crearLaboratorio(@RequestBody Laboratorio nuevoLaboratorio) {
        return repository.save(nuevoLaboratorio); // Guarda en Mongo el objeto que recibe
    }


    @PutMapping("/{id}")
    public Laboratorio actualizarLaboratorio(@PathVariable String id, @RequestBody Laboratorio detallesLaboratorio) {
        // Primero buscamos si el laboratorio existe en la base de datos
        return repository.findById(id)
                .map(laboratorioExistente -> {
                    // Si existe, actualizamos sus datos con la información nueva
                    laboratorioExistente.setNombreEmpresa(detallesLaboratorio.getNombreEmpresa());
                    laboratorioExistente.setTelefonoContacto(detallesLaboratorio.getTelefonoContacto());
                    laboratorioExistente.setEmailPedidos(detallesLaboratorio.getEmailPedidos());
                    laboratorioExistente.setDireccionFiscal(detallesLaboratorio.getDireccionFiscal());

                    // Guardamos los cambios en MongoDB
                    return repository.save(laboratorioExistente);
                })
                .orElseThrow(() -> new RuntimeException("Laboratorio no encontrado con ID: " + id));
    }


    @DeleteMapping("/{id}")
    public String borrarLaboratorio(@PathVariable String id) {
        // Comprobamos que el ID exista antes de intentar borrarlo
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return "Laboratorio eliminado correctamente.";
        } else {
            return "Error: El laboratorio no existe.";
        }
    }
}