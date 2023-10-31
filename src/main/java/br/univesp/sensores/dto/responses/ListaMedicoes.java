package br.univesp.sensores.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ListaMedicoes(
		Long idMedicao,
		BigDecimal vlTemperatura,
		BigDecimal vlUmidade,
		LocalDateTime dtMedicao
		) {

}