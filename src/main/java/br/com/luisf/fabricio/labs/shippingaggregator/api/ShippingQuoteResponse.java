package br.com.luisf.fabricio.labs.shippingaggregator.api;

import java.math.BigDecimal;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "ShippingQuoteResponse", description = "Aggregated shipping quote response ordered by lowest price.")
public record ShippingQuoteResponse(
        String route,
        String sku,
        int quantity,
        BigDecimal orderValue,
        List<ShippingQuoteOptionResponse> options,
        long totalAggregationTimeMs) {
}
