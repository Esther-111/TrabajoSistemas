package com.farmacia.erp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesechosPorMesDTO {
    // El periodo de tiempo. Ej: "2026-04", "2026-05" o "Mayo"
    private String mes;

    // La suma de la cantidad de unidades que se han desechado en ese mes
    private Integer totalUnidadesDesechadas;
}
