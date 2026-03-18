package org.cesde.velotax.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShipmentResponse {
    private Boolean success;
    private String trackingNumber;
    private BigDecimal estimatedCost;
    private String estimatedDays;
    private String shipmentId;
    private LocalDateTime createdAt;
}
