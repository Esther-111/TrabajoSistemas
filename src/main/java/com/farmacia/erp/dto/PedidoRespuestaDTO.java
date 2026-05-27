package com.farmacia.erp.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoRespuestaDTO {
    private String id;
    private String idLaboratorio;
    private List<DetallesPedidoDTO> listaMedicamentos;
    private LocalDateTime fechaRegistro;//Fecha y hora exactas en las que se procesó el pedido
    private Double precioTotal;//Precio total del pedido
}
