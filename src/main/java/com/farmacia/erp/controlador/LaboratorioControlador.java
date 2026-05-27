package com.farmacia.erp.controlador;

import com.farmacia.erp.entidades.Laboratorio;
import com.farmacia.erp.dto.LaboratorioCrearDTO;
import com.farmacia.erp.dto.LaboratorioRespuestaDTO; // Importamos tu nuevo DTO
import com.farmacia.erp.servicio.LaboratorioServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/laboratorios")
@RequiredArgsConstructor
public class LaboratorioControlador {

    private final LaboratorioServicio servicio;

    @GetMapping
    public List<LaboratorioRespuestaDTO> obtenerTodos() {
        // Buscamos los laboratorios y los transformamos al DTO de salida
        return servicio.listarTodos().stream()
                .map(lab -> new LaboratorioRespuestaDTO(
                        lab.getId(),
                        lab.getNombreEmpresa(),
                        lab.getTelefonoContacto(),
                        lab.getEmailPedidos(),
                        lab.getDireccionFiscal()
                ))
                .toList();
    }

    @PostMapping
    public Laboratorio crearLaboratorio(@RequestBody LaboratorioCrearDTO dto) {
        Laboratorio nuevoLaboratorio = new Laboratorio();
        nuevoLaboratorio.setNombreEmpresa(dto.getNombreEmpresa());
        nuevoLaboratorio.setTelefonoContacto(dto.getTelefonoContacto());
        nuevoLaboratorio.setEmailPedidos(dto.getEmailPedidos());
        nuevoLaboratorio.setDireccionFiscal(dto.getDireccionFiscal());

        return servicio.guardar(nuevoLaboratorio);
    }

    @PutMapping("/{id}")
    public Laboratorio actualizarLaboratorio(@PathVariable String id, @RequestBody LaboratorioCrearDTO dto) {
        return servicio.buscarPorId(id).map(existente -> {
            existente.setNombreEmpresa(dto.getNombreEmpresa());
            existente.setTelefonoContacto(dto.getTelefonoContacto());
            existente.setEmailPedidos(dto.getEmailPedidos());
            existente.setDireccionFiscal(dto.getDireccionFiscal());
            return servicio.guardar(existente);
        }).orElseThrow(() -> new RuntimeException("Laboratorio no encontrado"));
    }

    @DeleteMapping("/{id}")
    public String borrarLaboratorio(@PathVariable String id) {
        servicio.eliminar(id);
        return "Laboratorio eliminado correctamente.";
    }
}
