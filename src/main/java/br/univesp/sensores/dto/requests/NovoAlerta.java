package br.univesp.sensores.dto.requests;

import java.math.BigDecimal;

public record NovoAlerta(
		Integer tipoAlerta,
		Integer intervaloEsperaSegundos,
		BigDecimal vlMax,
		BigDecimal vlMin
		) {

}
