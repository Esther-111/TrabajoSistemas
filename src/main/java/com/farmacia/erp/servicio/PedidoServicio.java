package com.farmacia.erp.servicio;

import com.farmacia.erp.entidades.DetallePedido;
import com.farmacia.erp.entidades.Medicamento;
import com.farmacia.erp.entidades.Pedido;
import com.farmacia.erp.repositorio.PedidoRepositorio;
import com.farmacia.erp.repositorio.MedicamentoRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service       //le dice a Spring Boot: esta clase es el 'cerebro' de mi farmacia donde están las reglas de negocio
@RequiredArgsConstructor    // es para Lombok: constructor con los args requeridos
public class PedidoServicio {
    private final PedidoRepositorio pedidoRepositorio;
    private final MedicamentoRepositorio medicamentoRepositorio;

    /* @Transactional asegura q si algo falla a mitad de la compra, se cancele todo para no dejar
       la bbdd a medias
      Este método procesa el carrito de la compra: suma el stock nuevo, calcula el precio total y guarda el ticket*/
    @Transactional      //convierte lo q pasa en ese método en una transaccion blindada
    public Pedido realizarPedido(String idLaboratorio, List<DetallePedido> detalles) {
        double costeTotal = 0.0;

        // recorre uno por uno los productos que estamos comprando
        for (DetallePedido detalle : detalles) {
            Medicamento med = medicamentoRepositorio.findById(detalle.getIdMedicamento())
                    .orElseThrow(() -> new RuntimeException("Medicamento no registrado: " + detalle.getIdMedicamento()));

            // Suma las unidades compradas al stock de la estantería
            med.setCantidadStock(med.getCantidadStock() + detalle.getCantidad());
            medicamentoRepositorio.save(med);

            // multiplica el precio de la caja por las unidades compradas y lo suma al ticket
            costeTotal += (med.getPrecio() * detalle.getCantidad());
        }

        // crea el ticket de compra definitivo con la fecha de hoy y lo guarda en el sistema
        Pedido pedido = new Pedido();
        pedido.setIdLaboratorio(idLaboratorio);
        pedido.setFechaPedido(LocalDate.now());
        pedido.setListaMedicamentos(detalles);
        pedido.setCosteTotal(costeTotal);

        return pedidoRepositorio.save(pedido);
    }

    // consulta el historial con todas las compras realizadas por la farmacia
    public List<Pedido> listarTodos() {
        return pedidoRepositorio.findAll();
    }
}
