package com.farmacia.erp.controlador;

import com.farmacia.erp.entidades.Pedido;
import com.farmacia.erp.repositorio.PedidoRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
        import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoControlador {

    @Autowired
    private PedidoRepositorio repository;

    @GetMapping
    public List<Pedido> obtenerTodos() {
        return repository.findAll();
    }

    @PostMapping
    public Pedido crearPedido(@RequestBody Pedido pedido) {
        return repository.save(pedido);
    }

    @PutMapping("/{id}")
    public Pedido actualizarPedido(@PathVariable String id, @RequestBody Pedido detalles) {
        return repository.findById(id)
                .map(existente -> {
                    existente.setFechaPedido(detalles.getFechaPedido());
                    existente.setCosteTotal(detalles.getCosteTotal());
                    existente.setIdLaboratorio(detalles.getIdLaboratorio());
                    existente.setListaMedicamentos(detalles.getListaMedicamentos());
                    return repository.save(existente);
                })
                .orElseThrow(() -> new RuntimeException("No encontrado"));
    }

    @DeleteMapping("/{id}")
    public String borrarPedido(@PathVariable String id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return "Eliminado";
        }
        return "No encontrado";
    }
}