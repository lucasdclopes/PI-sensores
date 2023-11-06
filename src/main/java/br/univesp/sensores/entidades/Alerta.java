package br.univesp.sensores.entidades;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.jboss.logging.Logger;

import br.univesp.sensores.erros.ErroNegocioException;
import br.univesp.sensores.helpers.ConfigHelper;
import br.univesp.sensores.helpers.ConfigHelper.Chaves;
import br.univesp.sensores.helpers.EnumHelper;
import br.univesp.sensores.helpers.EnumHelper.IEnumDescritivel;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.persistence.CascadeType;
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
	private String destinatarios;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "alerta", orphanRemoval = true, cascade = {CascadeType.PERSIST,CascadeType.MERGE})
	private Set<AlertaEnviado> alertasEnviados = new HashSet<>();
	
	private final static Integer INTERVALO_MIN = ConfigHelper.getInstance()
			.getConfigInteger(Chaves.ALERTA_INTERVALO_MIN);
	
	/**
	 * Construtor exclusivo para o framework Jakarta.
	 */
	@Deprecated
	public Alerta() {}

	public Alerta(TipoAlerta tipoAlerta, Integer intervaloEsperaSegundos, BigDecimal vlMax, BigDecimal vlMin, String destinatarios) {
		if (vlMax == null && vlMin == null)
			throw new ErroNegocioException("Pelo menos o valor mínimo ou valor máximo precisa estar preenchido. Ambos estão vazis");
		
		validarIntervalo(intervaloEsperaSegundos);
		validarEmails(destinatarios);
		
		this.tipoAlerta = tipoAlerta.getCodigo();
		this.intervaloEsperaSegundos = intervaloEsperaSegundos;
		this.vlMax = vlMax;
		this.vlMin = vlMin;
		this.isHabilitado = true;
		this.dtCriado = LocalDateTime.now();
		this.destinatarios = destinatarios;
	}
	
	public void habilitar() {
		this.isHabilitado = true;
	}
	
	public void desabilitar() {
		this.isHabilitado = false;
	}
	
	public void alterarDestinatarios(String destinatarios) {
		validarEmails(destinatarios);
		this.destinatarios = destinatarios;
	}
	
	public void alterarIntervalo(Integer intervalo) {
		validarIntervalo(intervalo);
		this.intervaloEsperaSegundos = intervalo;
		
	}	
	
	public void enviarAlerta(MedicaoSensor medicao) {
		TipoAlerta tipoAlerta = EnumHelper.getEnumFromCodigo(this.tipoAlerta,TipoAlerta.class);
		Boolean enviar = switch (tipoAlerta) {
		case TEMPERATURA -> checkRange(medicao.getVlTemperatura());
		case UMIDADE -> checkRange(medicao.getVlUmidade());
		default -> throw new ErroNegocioException("Não existe definição para o tipo de alerta (" + tipoAlerta.toString() + ") informado");
		};
		
		if (enviar) {
			this.alertasEnviados.add(new AlertaEnviado(this, LocalDateTime.now()));
			LOGGER.fatal("simulando envio do alerta para " + this.destinatarios);
		}
	
	}
	
	private void validarIntervalo(Integer intervalo) {
		if (intervalo < INTERVALO_MIN)
			throw new ErroNegocioException(
					String.format("O tempo de espera entre alertas não pode ser menor do que %s segundos",INTERVALO_MIN));
		
	}
	
	private Boolean checkRange(BigDecimal vlMedicao) {
		return (vlMax != null && vlMedicao.compareTo(vlMax) > 0)
				|| (vlMin != null && vlMedicao.compareTo(vlMin) < 0);
	}
	
	private void validarEmails(String destinatarios) {
		Set<String> emails = new HashSet<String>(Arrays.asList(destinatarios.split(";"))); //jogar em um SET elimina os duplicados
		emails.forEach(mail -> {
			try {
				new InternetAddress(mail).validate();
			} catch (AddressException e) {
				throw new ErroNegocioException("O endereço de email ("+ mail +") informado é inválido");
			}
		});
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
	
	public String getDestinatarios() {
		return destinatarios;
	}

	public Set<AlertaEnviado> getAlertasEnviados() {
		return alertasEnviados;
	}

}
