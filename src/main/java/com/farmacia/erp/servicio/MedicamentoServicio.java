package com.farmacia.erp.servicio;

import com.farmacia.erp.entidades.Medicamento;
import com.farmacia.erp.repositorio.MedicamentoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedicamentoServicio {
    private final MedicamentoRepositorio medicamentoRepositorio;

    // añade un nuevo medicamento al catálogo o modifica los datos de uno existente
    public Medicamento guardar(Medicamento medicamento) {
        return medicamentoRepositorio.save(medicamento);
    }

    // nuestra todo el inventario de medicamentos que tiene la farmacia
    public List<Medicamento> listarTodos() {
        return medicamentoRepositorio.findAll();
    }

    // localiza producto específico en el almacén mediante su ID
    public Optional<Medicamento> buscarPorId(String id) {
        return medicamentoRepositorio.findById(id);
    }

    public void eliminar(String id) {
        medicamentoRepositorio.deleteById(id);
    }

    // busca y devuelve todos los medicamentos alternativos que tengan la misma sust (ej. buscar todos los que tengan "Ibuprofeno")
    public List<Medicamento> buscarPorPrincipioActivo(String principioActivo) {
        return medicamentoRepositorio.findAll().stream()
                .filter(m -> m.getPrincipioActivo().equalsIgnoreCase(principioActivo))
                .toList();
    }
}
