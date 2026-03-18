package org.cesde.velotax.controller;

import org.cesde.velotax.dto.*;
import org.cesde.velotax.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quotes")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class QuoteController {
    
    @Autowired
    private QuoteService quoteService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<QuoteResponse>> getQuote(
            @RequestBody QuoteRequest request) {
        try {
            // TODO: Obtener usuario del contexto de seguridad
            org.cesde.velotax.entity.User dummyUser = org.cesde.velotax.entity.User.builder()
                    .id(2L)
                    .email("usuario@example.com")
                    .build();
            
            QuoteResponse response = quoteService.getQuote(request, dummyUser);
            return ResponseEntity.ok(ApiResponse.success("Cotización calculada", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Error al calcular cotización", e.getMessage()));
        }
    }
}
