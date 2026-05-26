package com.farmacia.erp.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "laboratorios")
public class Laboratorio {

    @Id
    private String id;

    private String nombreEmpresa;
    private String telefonoContacto;
    private String emailPedidos;
    private String direccionFiscal;
}