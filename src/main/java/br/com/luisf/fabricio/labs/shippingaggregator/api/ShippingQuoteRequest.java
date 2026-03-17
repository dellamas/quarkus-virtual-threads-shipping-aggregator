package br.com.luisf.fabricio.labs.shippingaggregator.api;

import java.math.BigDecimal;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "ShippingQuoteRequest", description = "Request payload to aggregate shipping quotes across partner carriers.")
public record ShippingQuoteRequest(
        @Schema(example = "sao-paulo-sp") String origin,
        @Schema(example = "belo-horizonte-mg") String destination,
        @Schema(example = "SKU-9001") String sku,
        @Schema(example = "3") int quantity,
        @Schema(example = "799.90") BigDecimal orderValue) {
}
