package org.cesde.velotax.service;

import org.cesde.velotax.dto.*;
import org.cesde.velotax.entity.*;
import org.cesde.velotax.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class ShipmentService {
    
    @Autowired
    private ShipmentRepository shipmentRepository;
    
    @Autowired
    private ShipmentTimelineRepository timelineRepository;
    
    @Autowired
    private RecipientRepository recipientRepository;
    
    private static final BigDecimal COST_EXPRESS = BigDecimal.valueOf(25);
    private static final BigDecimal COST_PREMIUM = BigDecimal.valueOf(18);
    private static final BigDecimal COST_STANDARD = BigDecimal.valueOf(12);
    private static final BigDecimal COST_ECONOMIC = BigDecimal.valueOf(8);
    
    private static final BigDecimal DISTANCE_FACTOR = BigDecimal.valueOf(1.2);
    private static final BigDecimal INSURANCE_RATE = BigDecimal.valueOf(0.02);
    
    public CreateShipmentResponse createShipment(CreateShipmentRequest request, User user) {
        // Generar número de seguimiento
        String trackingNumber = generateTrackingNumber();
        
        // Calcular costo
        BigDecimal estimatedCost = calculateCost(
            request.getWeight(),
            request.getServiceType(),
            request.getOrigin(),
            request.getDestination(),
            request.getInsurance() != null && request.getInsurance()
        );
        
        // Obtener días estimados
        String estimatedDays = getEstimatedDays(request.getServiceType());
        
        // Crear o buscar destinatario
        Recipient recipient = Recipient.builder()
                .user(user)
                .fullName(request.getRecipient())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .build();
        recipientRepository.save(recipient);
        
        // Crear envío
        Shipment shipment = Shipment.builder()
                .trackingNumber(trackingNumber)
                .user(user)
                .recipient(recipient)
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .totalWeight(request.getWeight())
                .serviceType(Shipment.ServiceType.valueOf(request.getServiceType().toUpperCase()))
                .totalValueDeclared(request.getValueDeclaration())
                .insurance(request.getInsurance() != null && request.getInsurance())
                .status(Shipment.ShipmentStatus.PENDIENTE)
                .estimatedCost(estimatedCost)
                .estimatedDeliveryDate(calculateDeliveryDate(request.getServiceType()))
                .build();
        
        shipmentRepository.save(shipment);
        
        // Crear timeline inicial
        ShipmentTimeline timeline = ShipmentTimeline.builder()
                .shipment(shipment)
                .status(ShipmentTimeline.TimelineStatus.PENDIENTE)
                .location("Centro de Distribución " + request.getOrigin())
                .description("Envío registrado en el sistema")
                .build();
        
        timelineRepository.save(timeline);
        
        return CreateShipmentResponse.builder()
                .success(true)
                .trackingNumber(trackingNumber)
                .estimatedCost(estimatedCost)
                .estimatedDays(estimatedDays)
                .shipmentId(String.valueOf(shipment.getId()))
                .createdAt(LocalDateTime.now())
                .build();
    }
    
    public ShipmentResponse getShipmentByTrackingNumber(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new RuntimeException("Envío no encontrado"));
        
        List<ShipmentTimeline> timeline = timelineRepository.findByShipmentIdOrderByTimestampDesc(shipment.getId());
        
        return ShipmentResponse.builder()
                .trackingNumber(shipment.getTrackingNumber())
                .status(shipment.getStatus().toString())
                .origin(shipment.getOrigin())
                .destination(shipment.getDestination())
                .recipient(shipment.getRecipient().getFullName())
                .currentLocation(timeline.isEmpty() ? shipment.getOrigin() : timeline.get(0).getLocation())
                .estimatedDelivery(shipment.getEstimatedDeliveryDate().toString())
                .weight(shipment.getTotalWeight())
                .cost(shipment.getEstimatedCost())
                .timeline(timeline.stream()
                        .map(t -> ShipmentResponse.TimelineEvent.builder()
                                .date(t.getTimestamp())
                                .status(t.getStatus().toString())
                                .location(t.getLocation())
                                .build())
                        .toList())
                .build();
    }
    
    private String generateTrackingNumber() {
        long timestamp = System.currentTimeMillis() % 10000000000L;
        return String.format("VEL-%010d", timestamp);
    }
    
    private BigDecimal calculateCost(BigDecimal weight, String serviceType, String origin, 
                                     String destination, boolean hasInsurance) {
        BigDecimal costPerKg = getCostPerKg(serviceType);
        BigDecimal baseCost = weight.multiply(costPerKg);
        
        // Aplicar factor de distancia si origen != destino
        BigDecimal finalCost = !origin.equals(destination) 
                ? baseCost.multiply(DISTANCE_FACTOR)
                : baseCost;
        
        // Aplicar seguro si aplica
        if (hasInsurance) {
            BigDecimal insuranceCost = finalCost.multiply(INSURANCE_RATE);
            finalCost = finalCost.add(insuranceCost);
        }
        
        return finalCost.setScale(2, RoundingMode.HALF_UP);
    }
    
    private BigDecimal getCostPerKg(String serviceType) {
        return switch (serviceType.toLowerCase()) {
            case "express" -> COST_EXPRESS;
            case "premium" -> COST_PREMIUM;
            case "standard" -> COST_STANDARD;
            case "economic" -> COST_ECONOMIC;
            default -> COST_STANDARD;
        };
    }
    
    private String getEstimatedDays(String serviceType) {
        return switch (serviceType.toLowerCase()) {
            case "express" -> "0-1";
            case "premium" -> "1";
            case "standard" -> "2-3";
            case "economic" -> "4-5";
            default -> "2-3";
        };
    }
    
    private LocalDate calculateDeliveryDate(String serviceType) {
        int days = switch (serviceType.toLowerCase()) {
            case "express" -> 1;
            case "premium" -> 1;
            case "standard" -> 3;
            case "economic" -> 5;
            default -> 3;
        };
        return LocalDate.now().plusDays(days);
    }
}
