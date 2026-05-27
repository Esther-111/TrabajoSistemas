package com.farmacia.erp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetallesPedidoDTO {
    private String idMedicamento;
    private Integer cantidad;
}
