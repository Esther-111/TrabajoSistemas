package com.farmacia.erp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesechoRespuestaDTO {
    private String id;
    private String idMedicamento;
    private String nombreMedicamento;
    private Integer cantidadPerdida;
    private LocalDate fechaDesecho;//Fecha exacta en la que se dio de baja el producto
    private String motivo;//Causa de la pérdida de stock
}
