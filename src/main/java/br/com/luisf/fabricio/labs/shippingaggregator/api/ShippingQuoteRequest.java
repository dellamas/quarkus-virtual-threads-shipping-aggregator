package br.com.luisf.fabricio.demos.shippingaggregator.api;

import java.math.BigDecimal;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(name = "ShippingQuoteRequest", description = "Request payload to aggregate shipping quotes across partner carriers.")
public record ShippingQuoteRequest(
        @NotBlank @Schema(example = "sao-paulo-sp") String origin,
        @NotBlank @Schema(example = "belo-horizonte-mg") String destination,
        @NotBlank @Schema(example = "SKU-9001") String sku,
        @Min(1) @Schema(example = "3") int quantity,
        @NotNull @DecimalMin(value = "0.01") @Schema(example = "799.90") BigDecimal orderValue) {
}
