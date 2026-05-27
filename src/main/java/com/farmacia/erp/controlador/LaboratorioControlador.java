package com.farmacia.erp.controlador;

import com.farmacia.erp.entidades.Laboratorio;
import com.farmacia.erp.dto.LaboratorioCrearDTO;
import com.farmacia.erp.servicio.LaboratorioServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laboratorios")
@RequiredArgsConstructor
public class LaboratorioControlador {

    private final LaboratorioServicio servicio;

    @GetMapping
    public List<Laboratorio> obtenerTodos() {
        return servicio.listarTodos();
    }

    @PostMapping
    public Laboratorio crearLaboratorio(@RequestBody LaboratorioCrearDTO dto) {
        // Transformamos el DTO de entrada en una Entidad real
        Laboratorio nuevoLaboratorio = new Laboratorio();
        nuevoLaboratorio.setNombreEmpresa(dto.getNombreEmpresa());
        nuevoLaboratorio.setTelefonoContacto(dto.getTelefonoContacto());
        nuevoLaboratorio.setEmailPedidos(dto.getEmailPedidos());
        nuevoLaboratorio.setDireccionFiscal(dto.getDireccionFiscal());

        return servicio.guardar(nuevoLaboratorio);
    }

    @PutMapping("/{id}")
    public Laboratorio actualizarLaboratorio(@PathVariable String id, @RequestBody LaboratorioCrearDTO dto) {
        return servicio.buscarPorId(id).map(existente -> {
            existente.setNombreEmpresa(dto.getNombreEmpresa());
            existente.setTelefonoContacto(dto.getTelefonoContacto());
            existente.setEmailPedidos(dto.getEmailPedidos());
            existente.setDireccionFiscal(dto.getDireccionFiscal());
            return servicio.guardar(existente);
        }).orElseThrow(() -> new RuntimeException("Laboratorio no encontrado"));
    }

    @DeleteMapping("/{id}")
    public String borrarLaboratorio(@PathVariable String id) {
        servicio.eliminar(id);
        return "Laboratorio eliminado correctamente.";
    }
}
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