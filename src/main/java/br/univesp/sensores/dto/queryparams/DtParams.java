package br.univesp.sensores.dto.queryparams;

import java.time.LocalDateTime;

import jakarta.ws.rs.QueryParam;

public class DtParams {
	
	private @QueryParam("dtInicial") LocalDateTime dtInicial;
	private @QueryParam("dtFinal") LocalDateTime dtFinal;
	
	public DtParams() {}
	
	public LocalDateTime getDtInicial() {
		return dtInicial;
	}
	public LocalDateTime getDtFinal() {
		return dtFinal;
	}
}
