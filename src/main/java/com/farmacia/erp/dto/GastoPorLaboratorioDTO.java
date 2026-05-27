package com.farmacia.erp.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GastoPorLaboratorioDTO {
    // El nombre del laboratorio (más útil para el LLM y el gráfico que el ID)
    private String nombreLaboratorio;

    // La suma total de dinero gastado en pedidos a este laboratorio
    private Double totalGastado;
}
