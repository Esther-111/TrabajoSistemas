package com.farmacia.erp.servicio;

import com.farmacia.erp.entidades.Laboratorio;
import com.farmacia.erp.repositorio.LaboratorioRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LaboratorioServicio {
    private final LaboratorioRepositorio laboratorioRepositorio;

    // crea un nuevo proveedor en la bbdd o actualiza uno si ya existe
    public Laboratorio guardar(Laboratorio laboratorio) {
        return laboratorioRepositorio.save(laboratorio);
    }

    // devuelve la lista completa con todos los laboratorios que nos suministran
    public List<Laboratorio> listarTodos() {
        return laboratorioRepositorio.findAll();
    }

    // busca la ficha de un laboratorio en concreto utilizando su código ID
    public Optional<Laboratorio> buscarPorId(String id) {
        return laboratorioRepositorio.findById(id);
    }

    public void eliminar(String id) {
        laboratorioRepositorio.deleteById(id);
    }
}
