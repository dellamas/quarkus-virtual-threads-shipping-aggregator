package br.com.luisf.fabricio.labs.shippingaggregator.service;

import java.math.BigDecimal;

import br.com.luisf.fabricio.labs.shippingaggregator.model.PartnerCode;

public record ShippingQuote(PartnerCode partner, BigDecimal price, int estimatedDays, String serviceLevel, long latencyMs) {
}
