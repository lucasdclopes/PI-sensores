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
	
	public String getConfig(String chave) { 
		String valor = properties.getProperty(chave);
		if (valor == null) 
			throw new RuntimeException("A configuração " + chave  + " não existe no sistema");
		return valor;
	}
}
