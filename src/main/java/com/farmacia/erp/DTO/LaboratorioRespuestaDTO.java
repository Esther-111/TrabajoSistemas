package com.farmacia.erp.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaboratorioRespuestaDTO {
    private String id;      // importante para que la IA sepa el código del laboratorio
    private String nombreEmpresa;
    private String telefonoContacto;
    private String emailPedidos;
    private String direccionFiscal;
}
