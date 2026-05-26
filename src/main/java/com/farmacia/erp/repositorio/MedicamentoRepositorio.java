package com.farmacia.erp.repositorio;

import com.farmacia.erp.entidades.Medicamento;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicamentoRepositorio extends MongoRepository<Medicamento, String> {
}