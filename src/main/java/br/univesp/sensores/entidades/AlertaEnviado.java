package br.univesp.sensores.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "alerta_enviado")
public class AlertaEnviado implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idEnviado;
	private Long idAlerta;
	private LocalDateTime dtMedicao;
	
	/**
	 * Construtor exclusivo para o framework Jakarta.
	 */
	@Deprecated
	public AlertaEnviado() {}

	public AlertaEnviado(Long idAlerta, LocalDateTime dtMedicao) {
		super();
		this.idAlerta = idAlerta;
		this.dtMedicao = dtMedicao;
	}

	public Long getIdEnviado() {
		return idEnviado;
	}

	public Long getIdAlerta() {
		return idAlerta;
	}

	public LocalDateTime getDtMedicao() {
		return dtMedicao;
	}
	
	

}
