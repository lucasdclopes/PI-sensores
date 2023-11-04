package br.univesp.sensores.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import org.jboss.logging.Logger;

import br.univesp.sensores.erros.ErroNegocioException;
import br.univesp.sensores.helpers.EnumHelper;
import br.univesp.sensores.helpers.EnumHelper.IEnumDescritivel;
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
	
	private static final Logger LOGGER = Logger.getLogger( Alerta.class.getName());

	public enum TipoAlerta implements IEnumDescritivel {
		TEMPERATURA(1),
		UMIDADE(2);
		private Integer codigo;
		TipoAlerta(Integer codigo){
			this.codigo = codigo;
		}
		@Override
		public Integer getCodigo() {
			return this.codigo;		
		}
		@Override
		public String getDescricao() {
			return "Tipo de alerta";
		}
	}
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idAlerta;
	private Boolean isHabilitado;
	private Integer tipoAlerta;
	private Integer intervaloEsperaSegundos;
	private BigDecimal vlMax;
	private BigDecimal vlMin;
	private LocalDateTime dtCriado;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "alerta", orphanRemoval = true)
	private Set<AlertaEnviado> alertasEnviados = new HashSet<>();
	
	private final static Integer INTERVALO_MIN = 240;
	
	/**
	 * Construtor exclusivo para o framework Jakarta.
	 */
	@Deprecated
	public Alerta() {}

	public Alerta(TipoAlerta tipoAlerta, Integer intervaloEsperaSegundos, BigDecimal vlMax, BigDecimal vlMin) {
		super();
		if (intervaloEsperaSegundos < INTERVALO_MIN)
			throw new ErroNegocioException(
					String.format("O tempo de espera entre alertas não pode ser menor do que %s segundos",INTERVALO_MIN));
		
		if (vlMax == null && vlMin == null)
			throw new ErroNegocioException("Pelo menos o valor mínimo ou valor máximo precisa estar preenchido. Ambos estão vazis");
		
		this.tipoAlerta = tipoAlerta.getCodigo();
		this.intervaloEsperaSegundos = intervaloEsperaSegundos;
		this.vlMax = vlMax;
		this.vlMin = vlMin;
		this.isHabilitado = true;
		this.dtCriado = LocalDateTime.now();
	}
	
	public void habilitar() {
		this.isHabilitado = true;
	}
	
	public void desabilitar() {
		this.isHabilitado = false;
	}
	
	public void enviarAlerta(MedicaoSensor medicao) {
		TipoAlerta tipoAlerta = EnumHelper.getEnumFromCodigo(this.tipoAlerta,TipoAlerta.class);
		Boolean enviar = switch (tipoAlerta) {
		case TEMPERATURA -> checkRange(medicao.getVlTemperatura());
		case UMIDADE -> checkRange(medicao.getVlUmidade());
		default -> throw new ErroNegocioException("Não existe definição para o tipo de alerta (" + tipoAlerta.toString() + ") informado");
		};
		
		if (enviar)
			LOGGER.fatal("simulando envio do alerta...");
	
	}
	
	private Boolean checkRange(BigDecimal vlMedicao) {
		return (vlMax != null && vlMedicao.compareTo(vlMax) > 0)
				|| (vlMin != null && vlMedicao.compareTo(vlMin) < 0);
	}
	
	public Long getIdAlerta() {
		return idAlerta;
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
	
	public LocalDateTime getDtCriado() {
		return dtCriado;
	}

	public Set<AlertaEnviado> getAlertasEnviados() {
		return alertasEnviados;
	}	


}
