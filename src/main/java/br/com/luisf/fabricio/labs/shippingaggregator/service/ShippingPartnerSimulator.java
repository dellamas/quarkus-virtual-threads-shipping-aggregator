package br.com.luisf.fabricio.labs.shippingaggregator.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;

import jakarta.enterprise.context.ApplicationScoped;

import br.com.luisf.fabricio.labs.shippingaggregator.api.ShippingQuoteRequest;
import br.com.luisf.fabricio.labs.shippingaggregator.model.PartnerCode;

@ApplicationScoped
public class ShippingPartnerSimulator {
    public ShippingQuote fetchQuote(PartnerCode partner, ShippingQuoteRequest request) {
        long startedAt = System.nanoTime();
        sleep(latencyFor(partner, request));
        long latencyMs = Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
        return new ShippingQuote(
                partner,
                priceFor(partner, request),
                daysFor(partner, request),
                serviceLevelFor(partner),
                latencyMs);
    }

    private void sleep(long latencyMs) {
        try {
            Thread.sleep(latencyMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Partner simulation interrupted", e);
        }
    }

    private long latencyFor(PartnerCode partner, ShippingQuoteRequest request) {
        int volumeFactor = Math.max(1, request.quantity());
        return switch (partner) {
            case FASTBOX -> 140L + (volumeFactor * 10L);
            case ECONOSHIP -> 220L + (volumeFactor * 12L);
            case PICKNPACK -> 180L + (volumeFactor * 15L);
        };
    }

    private BigDecimal priceFor(PartnerCode partner, ShippingQuoteRequest request) {
        BigDecimal base = switch (partner) {
            case FASTBOX -> new BigDecimal("39.90");
            case ECONOSHIP -> new BigDecimal("24.90");
            case PICKNPACK -> new BigDecimal("31.50");
        };
        BigDecimal quantityCost = BigDecimal.valueOf(request.quantity()).multiply(new BigDecimal("1.75"));
        BigDecimal valueFactor = request.orderValue().multiply(new BigDecimal("0.012"));
        BigDecimal partnerAdjustment = switch (partner) {
            case FASTBOX -> new BigDecimal("8.40");
            case ECONOSHIP -> new BigDecimal("0.00");
            case PICKNPACK -> new BigDecimal("4.25");
        };
        return base.add(quantityCost).add(valueFactor).add(partnerAdjustment).setScale(2, RoundingMode.HALF_UP);
    }

    private int daysFor(PartnerCode partner, ShippingQuoteRequest request) {
        int quantityAdjustment = request.quantity() > 4 ? 1 : 0;
        return switch (partner) {
            case FASTBOX -> 1 + quantityAdjustment;
            case ECONOSHIP -> 4 + quantityAdjustment;
            case PICKNPACK -> 2 + quantityAdjustment;
        };
    }

    private String serviceLevelFor(PartnerCode partner) {
        return switch (partner) {
            case FASTBOX -> "EXPRESS";
            case ECONOSHIP -> "ECONOMY";
            case PICKNPACK -> "STANDARD";
        };
    }
}
