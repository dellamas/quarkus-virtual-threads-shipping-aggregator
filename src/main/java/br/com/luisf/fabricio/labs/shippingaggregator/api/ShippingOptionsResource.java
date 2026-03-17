package br.com.luisf.fabricio.demos.shippingaggregator.api;

import java.util.Arrays;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import io.smallrye.common.annotation.RunOnVirtualThread;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import br.com.luisf.fabricio.demos.shippingaggregator.model.PartnerCode;
import br.com.luisf.fabricio.demos.shippingaggregator.service.ShippingDiagnosticsService;
import br.com.luisf.fabricio.demos.shippingaggregator.service.ShippingQuoteAggregationService;

@Path("/api/shipping-options")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Shipping Options", description = "Aggregate shipping partner quotes with Quarkus virtual threads.")
public class ShippingOptionsResource {
    @Inject
    ShippingQuoteAggregationService shippingQuoteAggregationService;

    @Inject
    ShippingDiagnosticsService shippingDiagnosticsService;

    @POST
    @Path("/quotes")
    @RunOnVirtualThread
    @Operation(summary = "Aggregate shipping quotes", description = "Collects blocking partner quotes in parallel with virtual threads and returns the cheapest option first.")
    @APIResponse(responseCode = "200", description = "Aggregated shipping quote response", content = @Content(schema = @Schema(implementation = ShippingQuoteResponse.class), examples = @ExampleObject(name = "quote", value = "{\"route\":\"sao-paulo-sp -> belo-horizonte-mg\",\"sku\":\"SKU-9001\",\"quantity\":3,\"orderValue\":799.90,\"options\":[{\"partner\":\"ECONOSHIP\",\"price\":40.25,\"estimatedDays\":4,\"serviceLevel\":\"ECONOMY\",\"latencyMs\":256}],\"totalAggregationTimeMs\":280}")))
    public ShippingQuoteResponse quote(ShippingQuoteRequest request) {
        return shippingQuoteAggregationService.aggregate(request);
    }

    @GET
    @Path("/partners")
    @Operation(summary = "List supported partners")
    @APIResponse(responseCode = "200", description = "Partner catalog", content = @Content(schema = @Schema(implementation = PartnerCatalogResponse.class, type = SchemaType.OBJECT)))
    public PartnerCatalogResponse partners() {
        return new PartnerCatalogResponse(Arrays.stream(PartnerCode.values()).map(Enum::name).toList());
    }

    @GET
    @Path("/diagnostics")
    @Operation(summary = "Read aggregator diagnostics")
    @APIResponse(responseCode = "200", description = "Current diagnostics", content = @Content(schema = @Schema(implementation = ShippingDiagnosticsResponse.class, type = SchemaType.OBJECT)))
    public ShippingDiagnosticsResponse diagnostics() {
        return shippingDiagnosticsService.snapshot();
    }
}
