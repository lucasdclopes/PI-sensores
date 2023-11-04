package br.univesp.sensores.dto.responses;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ListaAlertasResp(
		Long idAlerta,
		Boolean isHabilitado,
		Integer tipoAlerta,
		Integer intervaloEsperaSegundos,
		BigDecimal vlMax,
		BigDecimal vlMin,
		LocalDateTime dtCriado
		) {

}
