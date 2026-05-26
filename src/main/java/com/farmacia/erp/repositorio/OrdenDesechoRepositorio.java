package com.farmacia.erp.repositorio;

import com.farmacia.erp.entidades.OrdenDesechos;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenDesechoRepositorio extends MongoRepository<OrdenDesechos, String> {
}