package br.univesp.sensores.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MedicaoAgrpItemResp(
		Long idMedicao,
		BigDecimal vlTemperatura,
		BigDecimal vlUmidade,
		LocalDate dtMedicao
		) {

}
