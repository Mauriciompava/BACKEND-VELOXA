package org.cesde.velotax.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuoteResponse {
    private Boolean success;
    private QuoteData quote;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuoteData {
        private String origin;
        private String destination;
        private BigDecimal weight;
        private String serviceType;
        private BigDecimal baseCost;
        private BigDecimal distanceFactor;
        private BigDecimal totalCost;
        private String estimatedDays;
        private Breakdown breakdown;
        private LocalDateTime validUntil;
        
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Breakdown {
            private BigDecimal weightCost;
            private BigDecimal distanceSurcharge;
        }
    }
}
