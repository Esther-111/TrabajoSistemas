package com.farmacia.erp.servicio;

import com.farmacia.erp.dto.DetallePedidoDTO;
import com.farmacia.erp.entidades.DetallePedido;
import com.farmacia.erp.entidades.Medicamento;
import com.farmacia.erp.entidades.Pedido;
import com.farmacia.erp.dto.DetallePedidoDTO; // Importamos tu DTO
import com.farmacia.erp.repositorio.PedidoRepositorio;
import com.farmacia.erp.repositorio.MedicamentoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoServicio {
    private final PedidoRepositorio pedidoRepositorio;
    private final MedicamentoRepositorio medicamentoRepositorio;

    @Transactional
    public Pedido realizarPedido(String idLaboratorio, List<DetallePedidoDTO> detalles) {
        double costeTotal = 0.0;

        // 1. Creamos una lista vacía para ir guardando las entidades traducidas
        List<DetallePedido> detallesEntidad = new ArrayList<>();

        for (DetallePedidoDTO detalleDTO : detalles) {
            Medicamento med = medicamentoRepositorio.findById(detalleDTO.getIdMedicamento())
                    .orElseThrow(() -> new RuntimeException("Medicamento no registrado: " + detalleDTO.getIdMedicamento()));

            // Actualizamos stock
            med.setCantidadStock(med.getCantidadStock() + detalleDTO.getCantidad());
            medicamentoRepositorio.save(med);

            // Sumamos al coste total
            costeTotal += (med.getPrecio() * detalleDTO.getCantidad());

            // 2. Traducimos el DTO a Entidad y lo metemos en la nueva lista
            DetallePedido entidad = new DetallePedido();
            entidad.setIdMedicamento(detalleDTO.getIdMedicamento());
            entidad.setCantidad(detalleDTO.getCantidad());
            detallesEntidad.add(entidad);
        }

        Pedido pedido = new Pedido();
        pedido.setIdLaboratorio(idLaboratorio);
        pedido.setFechaPedido(LocalDate.now());

        // 3. Le pasamos la lista de entidades, solucionando el error
        pedido.setListaMedicamentos(detallesEntidad);
        pedido.setCosteTotal(costeTotal);

        return pedidoRepositorio.save(pedido);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepositorio.findAll();
    }
}