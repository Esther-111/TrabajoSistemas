package com.farmacia.erp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesechoCrearDTO {
    private String idMedicamento;// Id del medicamento que se va a destruir
    private Integer cantidadPerdida;// nº de unidades defectuosas o caducadas
}
