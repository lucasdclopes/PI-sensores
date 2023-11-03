package br.univesp.sensores.entidades;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "log_erros_sistema")
public class LogErrosSistema {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idLogErros;
	private String msgErro;
	private LocalDateTime dtLog;
	private String stacktrace;
	
	/**
	 * Construtor exclusivo para o framework Jakarta.
	 */
	@Deprecated
	public LogErrosSistema() {}

	public LogErrosSistema(String msgErro, LocalDateTime dtLog, String stacktrace) {
		super();
		this.msgErro = msgErro;
		this.dtLog = dtLog;
		this.stacktrace = stacktrace;
	}

	public Integer getIdLogErros() {
		return idLogErros;
	}

	public String getMsgErro() {
		return msgErro;
	}

	public LocalDateTime getDtLog() {
		return dtLog;
	}

	public String getStacktrace() {
		return stacktrace;
	}

}
