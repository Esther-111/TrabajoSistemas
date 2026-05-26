package com.farmacia.erp.entidades;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedido {

    private String idMedicamento;
    private Integer cantidad;
}