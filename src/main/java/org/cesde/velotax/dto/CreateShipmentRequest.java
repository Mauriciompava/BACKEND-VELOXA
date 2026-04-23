package org.cesde.velotax.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShipmentRequest {
    private String origin;
    private String destination;
    private BigDecimal weight;
    private String serviceType;
    private String recipient;
    private String phone;
    private String email;
    private String address;
    private String items;
    private BigDecimal valueDeclaration;
    private Boolean insurance;
}
