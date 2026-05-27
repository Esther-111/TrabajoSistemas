package com.farmacia.erp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesechoCrearDTO {
    // ID del medicamento que se va a destruir
    private String idMedicamento;

    // nº de unidades defectuosas o caducadas
    private Integer cantidadPerdida;
}
