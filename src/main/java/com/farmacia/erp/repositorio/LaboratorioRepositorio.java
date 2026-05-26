package com.farmacia.erp.repositorio;

import com.farmacia.erp.entidades.Laboratorio;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LaboratorioRepositorio extends MongoRepository<Laboratorio, String> {

}