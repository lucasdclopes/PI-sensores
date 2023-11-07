package br.univesp.sensores.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigHelper {

	private final static InputStream input = Thread.currentThread()
			.getContextClassLoader()
			.getResourceAsStream("config.properties");
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
			properties.load(input);
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
}
