package org.cesde.velotax.controller;

import org.cesde.velotax.dto.*;
import org.cesde.velotax.service.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ContactController {
    
    @Autowired
    private ContactService contactService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<ContactResponse>> createContact(
            @RequestBody ContactRequest request) {
        try {
            ContactResponse response = contactService.createContact(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Mensaje recibido exitosamente", response));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Validación fallida", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Error al procesar contacto", e.getMessage()));
        }
    }
}
