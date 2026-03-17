package br.com.luisf.fabricio.labs.shippingaggregator.api;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "PartnerCatalogResponse", description = "Supported shipping partners exposed by the lab.")
public record PartnerCatalogResponse(List<String> partners) {
}
