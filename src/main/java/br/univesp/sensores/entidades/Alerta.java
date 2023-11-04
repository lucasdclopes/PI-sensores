package br.univesp.sensores.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import br.univesp.sensores.erros.ErroNegocioException;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "alerta")
public class Alerta implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idAlerta;
	private Boolean isHabilitado;
	private Integer tipoAlerta;
	private Integer intervaloEsperaSegundos;
	private BigDecimal vlMax;
	private BigDecimal vlMin;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "alerta", orphanRemoval = true)
	private Set<AlertaEnviado> alertasEnviados = new HashSet<>();
	
	private final static Integer INTERVALO_MIN = 240;
	
	/**
	 * Construtor exclusivo para o framework Jakarta.
	 */
	@Deprecated
	public Alerta() {}

	public Alerta(Integer tipoAlerta, Integer intervaloEsperaSegundos, BigDecimal vlMax, BigDecimal vlMin) {
		super();
		if (intervaloEsperaSegundos < INTERVALO_MIN)
			throw new ErroNegocioException(
					String.format("O tempo de espera entre alertas não pode ser menor do que %s segundos",INTERVALO_MIN));
		this.tipoAlerta = tipoAlerta;
		this.intervaloEsperaSegundos = intervaloEsperaSegundos;
		this.vlMax = vlMax;
		this.vlMin = vlMin;
		this.isHabilitado = true;
	}
	
	public void habilitar() {
		this.isHabilitado = true;
	}
	
	public void desabilitar() {
		this.isHabilitado = false;
	}

	public Integer getIdAlerta() {
		return idAlerta;
	}

	public void setIdAlerta(Integer idAlerta) {
		this.idAlerta = idAlerta;
	}

	public Boolean getIsHabilitado() {
		return isHabilitado;
	}

	public Integer getTipoAlerta() {
		return tipoAlerta;
	}

	public Integer getIntervaloEsperaSegundos() {
		return intervaloEsperaSegundos;
	}

	public BigDecimal getVlMax() {
		return vlMax;
	}

	public BigDecimal getVlMin() {
		return vlMin;
	}

	public Set<AlertaEnviado> getAlertasEnviados() {
		return alertasEnviados;
	}	

}
