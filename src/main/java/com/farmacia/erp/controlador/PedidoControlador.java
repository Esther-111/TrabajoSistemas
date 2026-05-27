package com.farmacia.erp.controlador;

import com.farmacia.erp.entidades.Pedido;
import com.farmacia.erp.dto.PedidoCrearDTO;
import com.farmacia.erp.servicio.PedidoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
public class PedidoControlador {

    private final PedidoServicio servicio;

    @GetMapping
    public List<Pedido> obtenerTodos() {
        return servicio.listarTodos();
    }

    @PostMapping
    public Pedido crearPedido(@RequestBody PedidoCrearDTO dto) {
        // ¡Aquí está la magia! Le pasamos los datos del DTO al Servicio para que él calcule el precio y el stock.
        return servicio.realizarPedido(dto.getIdLaboratorio(), dto.getListaMedicamentos());
    }

    // Los métodos PUT y DELETE en Pedidos a veces no se implementan por seguridad (un ticket de compra no se edita, se anula), pero lo dejamos por si el profesor lo pide.
}
