package com.farmacia.erp.repositorio;

import com.farmacia.erp.entidades.Pedido;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PedidoRepositorio extends MongoRepository<Pedido, String> {
}