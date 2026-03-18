package org.cesde.velotax.dto;

import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteRequest {
    private String origin;
    private String destination;
    private BigDecimal weight;
    private String serviceType;
}
