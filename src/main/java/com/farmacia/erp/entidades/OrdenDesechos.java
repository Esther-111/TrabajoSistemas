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
@Document(collection = "orden_desechos")
public class OrdenDesechos {

    @Id
    private String id;

    private String idMedicamento;
    private Integer cantidadPerdida;
    private LocalDate fechaBaja;
}