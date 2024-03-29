package br.univesp.sensores.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "medicao_sensor")
public class MedicaoSensor implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idMedicao;
	private BigDecimal vlTemperatura;
	private BigDecimal vlUmidade;
	private LocalDateTime dtMedicao ;
	
	/**
	 * Construtor exclusivo para o framework Jakarta.
	 */
	@Deprecated
	public MedicaoSensor() {}
	

	public MedicaoSensor(BigDecimal vlTemperatura, BigDecimal vlUmidade) {
		this.vlTemperatura = vlTemperatura;
		this.vlUmidade = vlUmidade;
		this.dtMedicao = LocalDateTime.now();
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getIdMedicao() {
		return idMedicao;
	}

	public BigDecimal getVlTemperatura() {
		return vlTemperatura;
	}

	public BigDecimal getVlUmidade() {
		return vlUmidade;
	}

	public LocalDateTime getDtMedicao() {
		return dtMedicao;
	}
	
	
}
