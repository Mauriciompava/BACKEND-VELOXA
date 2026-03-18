package org.cesde.velotax.service;

import org.cesde.velotax.dto.QuoteRequest;
import org.cesde.velotax.dto.QuoteResponse;
import org.cesde.velotax.entity.Quote;
import org.cesde.velotax.entity.User;
import org.cesde.velotax.repository.QuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@Transactional
public class QuoteService {
    
    @Autowired
    private QuoteRepository quoteRepository;
    
    private static final BigDecimal DISTANCE_FACTOR = BigDecimal.valueOf(1.2);
    
    public QuoteResponse getQuote(QuoteRequest request, User user) {
        BigDecimal costPerKg = getCostPerKg(request.getServiceType());
        BigDecimal baseCost = request.getWeight().multiply(costPerKg).setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal distanceFactor = !request.getOrigin().equals(request.getDestination()) 
                ? DISTANCE_FACTOR 
                : BigDecimal.ONE;
        
        BigDecimal distanceSurcharge = baseCost.multiply(distanceFactor.subtract(BigDecimal.ONE))
                .setScale(2, RoundingMode.HALF_UP);
        
        BigDecimal totalCost = baseCost.multiply(distanceFactor).setScale(2, RoundingMode.HALF_UP);
        
        String estimatedDays = getEstimatedDays(request.getServiceType());
        LocalDateTime validUntil = LocalDateTime.now().plusDays(3);
        
        // Guardar cotización
        Quote quote = Quote.builder()
                .user(user)
                .origin(request.getOrigin())
                .destination(request.getDestination())
                .weight(request.getWeight())
                .serviceType(request.getServiceType())
                .baseCost(baseCost)
                .distanceFactor(distanceFactor)
                .totalCost(totalCost)
                .estimatedDays(estimatedDays)
                .validUntil(validUntil)
                .build();
        
        quoteRepository.save(quote);
        
        return QuoteResponse.builder()
                .success(true)
                .quote(QuoteResponse.QuoteData.builder()
                        .origin(request.getOrigin())
                        .destination(request.getDestination())
                        .weight(request.getWeight())
                        .serviceType(request.getServiceType())
                        .baseCost(baseCost)
                        .distanceFactor(distanceFactor)
                        .totalCost(totalCost)
                        .estimatedDays(estimatedDays)
                        .breakdown(QuoteResponse.QuoteData.Breakdown.builder()
                                .weightCost(baseCost)
                                .distanceSurcharge(distanceSurcharge)
                                .build())
                        .validUntil(validUntil)
                        .build())
                .build();
    }
    
    private BigDecimal getCostPerKg(String serviceType) {
        return switch (serviceType.toLowerCase()) {
            case "express" -> BigDecimal.valueOf(25);
            case "premium" -> BigDecimal.valueOf(18);
            case "standard" -> BigDecimal.valueOf(12);
            case "economic" -> BigDecimal.valueOf(8);
            default -> BigDecimal.valueOf(12);
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
}
