package org.cesde.velotax.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactResponse {
    private Boolean success;
    private String message;
    private String contactId;
    private String ticketNumber;
}
