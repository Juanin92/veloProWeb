package com.veloProWeb.controller.communication;

import com.veloProWeb.model.Enum.AlertStatus;
import com.veloProWeb.model.dto.communication.AlertResponseDTO;
import com.veloProWeb.service.communication.interfaces.IAlertService;
import com.veloProWeb.util.ResponseMessage;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/alertas")
@AllArgsConstructor
@Validated
public class AlertController {

    private final IAlertService alertService;

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN', 'MASTER', 'WAREHOUSE')")
    public ResponseEntity<List<AlertResponseDTO>> getAlerts() {
        return ResponseEntity.ok(alertService.getAlerts());
    }

    @PutMapping()
    @PreAuthorize("hasAnyAuthority('MASTER', 'WAREHOUSE')")
    public ResponseEntity<Map<String, String>> handleStatus(@RequestParam
                                                            @NotNull(message = "ID de la alerta es obligatorio")
                                                            Long alertId, @RequestParam
                                                            @NotNull(message = "Debe seleccionar una opción")
                                                                AlertStatus action,
                                                            @AuthenticationPrincipal UserDetails userDetails) {
        alertService.updateAlertStatus(alertId, action, userDetails);
        return ResponseEntity.ok(ResponseMessage.message("Cambio de status correcto de la alerta"));
    }
}
