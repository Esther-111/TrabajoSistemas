package com.farmacia.erp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LaboratorioCrearDTO {
    private String nombreEmpresa;
    private String telefonoContacto;
    private String emailPedidos;
    private String direccionFiscal;
}
