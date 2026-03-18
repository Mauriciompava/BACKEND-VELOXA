package org.cesde.velotax.controller;

import org.cesde.velotax.dto.*;
import org.cesde.velotax.entity.User;
import org.cesde.velotax.service.ShipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ShipmentController {
    
    @Autowired
    private ShipmentService shipmentService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<CreateShipmentResponse>> createShipment(
            @RequestBody CreateShipmentRequest request) {
        try {
            // TODO: Obtener usuario del contexto de seguridad
            User dummyUser = User.builder()
                    .id(2L)
                    .email("usuario@example.com")
                    .build();
            
            CreateShipmentResponse response = shipmentService.createShipment(request, dummyUser);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Envío creado exitosamente", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Error al crear envío", e.getMessage()));
        }
    }
    
    @GetMapping("/{trackingNumber}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipment(
            @PathVariable String trackingNumber) {
        try {
            ShipmentResponse response = shipmentService.getShipmentByTrackingNumber(trackingNumber);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Envío no encontrado", e.getMessage()));
        }
    }
}
