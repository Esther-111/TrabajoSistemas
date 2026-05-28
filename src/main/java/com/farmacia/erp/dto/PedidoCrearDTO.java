package com.farmacia.erp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoCrearDTO {
    // identificador del laboratorio al que le hacemos la compra
    private String idLaboratorio;

    // el carrito de la compra con los códigos de medicamentos y cantidades
    private List<DetallePedidoDTO> listaMedicamentos;
}
