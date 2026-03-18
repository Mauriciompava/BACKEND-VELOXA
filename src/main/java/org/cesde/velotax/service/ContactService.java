package org.cesde.velotax.service;

import org.cesde.velotax.dto.ContactRequest;
import org.cesde.velotax.dto.ContactResponse;
import org.cesde.velotax.entity.Contact;
import org.cesde.velotax.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
public class ContactService {
    
    @Autowired
    private ContactRepository contactRepository;
    
    public ContactResponse createContact(ContactRequest request) {
        // Validar email
        if (!isValidEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email inválido");
        }
        
        // Generar ticket único
        String ticketNumber = generateTicketNumber();
        
        // Crear contacto
        Contact contact = Contact.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .company(request.getCompany())
                .subject(request.getSubject())
                .message(request.getMessage())
                .category(request.getCategory())
                .ticketNumber(ticketNumber)
                .status("Nuevo")
                .priority("Media")
                .build();
        
        contactRepository.save(contact);
        
        return ContactResponse.builder()
                .success(true)
                .message("Mensaje recibido exitosamente")
                .contactId(String.valueOf(contact.getId()))
                .ticketNumber(ticketNumber)
                .build();
    }
    
    private String generateTicketNumber() {
        long timestamp = System.currentTimeMillis() / 1000;
        return String.format("TKT-2026-%06d", timestamp % 1000000);
    }
    
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
