package com.farmacia.erp.servicio;

import com.farmacia.erp.entidades.Medicamento;
import com.farmacia.erp.entidades.OrdenDesechos;
import com.farmacia.erp.repositorio.OrdenDesechoRepositorio;
import com.farmacia.erp.repositorio.MedicamentoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrdenDesechosServicio {
    private final OrdenDesechoRepositorio ordenDesechosRepositorio;
    private final MedicamentoRepositorio medicamentoRepositorio;

    // retira del inventario los productos q ya no sirven (caducados/rotos) y genera un parte de baja
    @Transactional
    public OrdenDesechos registrarDesecho(String idMedicamento, Integer cantidadPerdida) {

        // busca el medicamento que queremos dar de baja
        Medicamento med = medicamentoRepositorio.findById(idMedicamento)
                .orElseThrow(() -> new RuntimeException("Medicamento no encontrado: " + idMedicamento));

        // control de seguridad: comprueba que no intente tirar más unidades de las que realmente tengo
        if (med.getCantidadStock() < cantidadPerdida) {
            throw new RuntimeException("Error: No puedes retirar más stock del que tienes disponible.");
        }

        // resta las unidades desechadas del stock disponible
        med.setCantidadStock(med.getCantidadStock() - cantidadPerdida);
        medicamentoRepositorio.save(med);

        // genera el documento que justifica la pérdida de ese material con la fecha actual
        OrdenDesechos desecho = new OrdenDesechos();
        desecho.setIdMedicamento(idMedicamento);
        desecho.setCantidadPerdida(cantidadPerdida);
        desecho.setFechaBaja(LocalDate.now());

        return ordenDesechosRepositorio.save(desecho);
    }

    // muestra el historial completo de todas las mermas que ha sufrido la farmacia
    public List<OrdenDesechos> listarTodas() {
        return ordenDesechosRepositorio.findAll();
    }
}
