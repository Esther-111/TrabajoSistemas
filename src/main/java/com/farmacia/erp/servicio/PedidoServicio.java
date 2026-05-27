package com.farmacia.erp.servicio;

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
    public Pedido realizarPedido(String idLaboratorio, List<DetallePedidoDTO> detallesDTO) {
        double costeTotal = 0.0;

        // Creamos la lista de entidades que se guardará en MongoDB
        List<DetallePedido> detallesEntidad = new ArrayList<>();

        for (DetallePedidoDTO dto : detallesDTO) {
            Medicamento med = medicamentoRepositorio.findById(dto.getIdMedicamento())
                    .orElseThrow(() -> new RuntimeException("Medicamento no registrado: " + dto.getIdMedicamento()));

            // Sumamos las unidades al stock
            med.setCantidadStock(med.getCantidadStock() + dto.getCantidad());
            medicamentoRepositorio.save(med);

            costeTotal += (med.getPrecio() * dto.getCantidad());

            // Convertimos vuestro DTO en la entidad DetallePedido
            detallesEntidad.add(new DetallePedido(dto.getIdMedicamento(), dto.getCantidad()));
        }

        Pedido pedido = new Pedido();
        pedido.setIdLaboratorio(idLaboratorio);
        pedido.setFechaPedido(LocalDate.now());
        pedido.setListaMedicamentos(detallesEntidad);
        pedido.setCosteTotal(costeTotal);

        return pedidoRepositorio.save(pedido);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepositorio.findAll();
    }
}