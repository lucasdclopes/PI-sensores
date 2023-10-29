package br.univesp.sensores.dto.requests;

import java.math.BigDecimal;

public record NovaMedicao(BigDecimal vlTemperatura,BigDecimal vlUmidade) {

}
