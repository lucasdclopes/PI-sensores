package br.univesp.sensores.helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import org.jboss.logging.Logger;

public class ConfigHelper {
	
	private static final Logger LOGGER = Logger.getLogger(ConfigHelper.class.getName());
	
	private final static ClassLoader loader = Thread.currentThread().getContextClassLoader();
	private final static InputStream inputConfigs = loader.getResourceAsStream("config.properties");
	private final static String EMAIL_ALERTA; 
	static {
		StringBuilder builder = new StringBuilder(2200);
		try (InputStream is = loader.getResourceAsStream("template_alerta.html");//src/main/resources
				InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
				BufferedReader reader = new BufferedReader(streamReader)) {

			String linha;
			while ((linha = reader.readLine()) != null) {
				builder.append(linha);
			}
		} catch (IOException e) {
			LOGGER.fatal("Não é possível inicializar o sistema por um problema no carregamento do html do email de alerta",e);
		}
		EMAIL_ALERTA = builder.toString();
	}
	
	private final static Properties properties = new Properties();
	private static ConfigHelper singleton = null;
	
	public enum Chaves {
		ALERTA_INTERVALO_MIN,
		CORS_URLS_PERMITIDAS,
		
		EMAIL_NOME_REMETENTE,
		EMAIL_ENDERECO_REMETENTE,
		EMAIL_SMTP_HOSTNAME,
		EMAIL_SMTP_PORTA,
		EMAIL_SMTP_USER,
		EMAIL_SMTP_SENHA,
		
		MONITORAMENTO_INTERVALO,
		PAGINACAO_MAX_ITENS, 
		SCHEDULER_ALERTA_INTERVALO,
		SCHEDULER_ALERTA_LIGADO,
		SIMULADOR_INTERVALO;
	}
	//singleton
	public static ConfigHelper getInstance() {
		ConfigHelper instancia = singleton;
		if (instancia == null) {
			synchronized (ConfigHelper.class) {
				instancia = singleton;
				if (instancia == null)
					singleton = instancia = new ConfigHelper();
				
			}
		}
		try {
			properties.load(inputConfigs);
		} catch (IOException e) {
			throw new RuntimeException("Não foi possível carregar as configurações do sistema, " + e.getMessage(),e);
		}
		return instancia;

	}
	
	public String getConfig(Chaves chave) { 
		String valor = properties.getProperty(chave.name());
		if (valor == null) 
			throw new RuntimeException("A configuração " + chave  + " não existe no sistema");
		return valor;
	}
	
	public Integer getConfigInteger(Chaves chave) { 
		try {
			return Integer.parseInt(getConfig(chave));
		} catch (NumberFormatException e) {
			throw new RuntimeException("O valor da chave " + chave  + " deveria ser numérico");
		}
	}
	
	public Boolean getConfigBoolean(Chaves chave) { 
		String valor = getConfig(chave);
		if (valor.equalsIgnoreCase("true"))
			return true;
		else if (valor.equalsIgnoreCase("false"))
			return false;
		else 
			throw new RuntimeException("O valor da chave " + chave  + " deveria ser true ou false");	
	}
	
	public String getEmailTemplateEmailAlerta() {
		return EMAIL_ALERTA;
	}
}
