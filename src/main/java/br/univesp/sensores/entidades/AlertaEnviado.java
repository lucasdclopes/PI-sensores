package br.univesp.sensores.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "alerta_enviado")
public class AlertaEnviado implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idEnviado;
	private LocalDateTime dtMedicao;
	
	@ManyToOne(optional = false)
	@JoinColumn(name = "idAlerta", nullable = false)
	private Alerta alerta;
	
	/**
	 * Construtor exclusivo para o framework Jakarta.
	 */
	@Deprecated
	public AlertaEnviado() {}

	public AlertaEnviado(Alerta alerta, LocalDateTime dtMedicao) {
		super();
		this.alerta = alerta;
		this.dtMedicao = dtMedicao;
	}

	public Long getIdEnviado() {
		return idEnviado;
	}
	
	public Alerta getAlerta() {
		return alerta;
	}

	public LocalDateTime getDtMedicao() {
		return dtMedicao;
	}
	
	

}
