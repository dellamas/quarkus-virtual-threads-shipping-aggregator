package br.com.luisf.fabricio.labs.shippingaggregator.api;

import java.math.BigDecimal;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import br.com.luisf.fabricio.labs.shippingaggregator.model.PartnerCode;

@Schema(name = "ShippingQuoteOptionResponse", description = "Single partner quote returned by the shipping aggregator.")
public record ShippingQuoteOptionResponse(
        PartnerCode partner,
        BigDecimal price,
        int estimatedDays,
        String serviceLevel,
        long latencyMs) {
}
