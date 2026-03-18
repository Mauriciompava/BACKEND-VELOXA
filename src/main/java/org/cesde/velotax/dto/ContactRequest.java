package org.cesde.velotax.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactRequest {
    private String fullName;
    private String email;
    private String phone;
    private String company;
    private String subject;
    private String message;
    private String category;
}
