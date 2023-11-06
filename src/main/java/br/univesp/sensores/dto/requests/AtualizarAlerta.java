package br.univesp.sensores.dto.requests;

public record AtualizarAlerta(
		Integer intervaloEsperaSegundos,
		String destinatarios,
		Boolean isHabilitado
		) {

}
