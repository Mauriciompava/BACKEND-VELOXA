package org.cesde.velotax.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipmentResponse {
    private String trackingNumber;
    private String status;
    private String origin;
    private String destination;
    private String recipient;
    private String currentLocation;
    private String estimatedDelivery;
    private BigDecimal weight;
    private BigDecimal cost;
    private List<TimelineEvent> timeline;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TimelineEvent {
        private LocalDateTime date;
        private String status;
        private String location;
    }
}
