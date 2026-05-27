package com.farmacia.erp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentoRespuestaDTO {
    private String id;     // importante para que la IA lo referencie
    private String idLaboratorio;
    private String nombreComercial;
    private String principioActivo;
    private String dosis;
    private String formaFarmaceutica;
    private Integer cantidadStock;
    private Double precio;
    private LocalDate fechaCaducidad;
}
