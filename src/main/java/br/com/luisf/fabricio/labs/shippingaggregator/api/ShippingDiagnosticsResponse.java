package br.com.luisf.fabricio.demos.shippingaggregator.api;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "ShippingDiagnosticsResponse", description = "Runtime counters of the shipping quote aggregator.")
public record ShippingDiagnosticsResponse(long totalQuotes, List<PartnerDiagnosticsResponse> partners) {
}
