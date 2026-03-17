package br.com.luisf.fabricio.demos.shippingaggregator.api;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import br.com.luisf.fabricio.demos.shippingaggregator.model.PartnerCode;

@Schema(name = "PartnerDiagnosticsResponse", description = "Per-partner diagnostics for simulated shipping partner calls.")
public record PartnerDiagnosticsResponse(
        PartnerCode partner,
        long calls,
        long averageLatencyMs) {
}
