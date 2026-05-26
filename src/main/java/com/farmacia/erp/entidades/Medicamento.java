package com.farmacia.erp.entidades;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "medicamentos")
public class Medicamento {

    @Id
    private String id;

    private String idLaboratorio;

    private String nombreComercial;
    private String principioActivo;
    private String dosis;
    private String formaFarmaceutica;
    private Integer cantidadStock;
    private Double precio;
    private LocalDate fechaCaducidad;
}