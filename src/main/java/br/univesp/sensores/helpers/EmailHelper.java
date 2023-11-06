package br.univesp.sensores.helpers;

import java.time.LocalDateTime;
import java.util.Set;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import br.univesp.sensores.dao.LogErrosDao;
import br.univesp.sensores.entidades.LogErrosSistema;
import br.univesp.sensores.helpers.ConfigHelper.Chaves;
import jakarta.ejb.Stateless;
import jakarta.enterprise.concurrent.ManagedThreadFactory;
import jakarta.inject.Inject;

@Stateless
public class EmailHelper {
	
	@Inject private LogErrosDao errosDao;

	public void enviarEmail(Set<String> emails) {
		
		ConfigHelper config = ConfigHelper.getInstance();
		HtmlEmail email = new HtmlEmail();
		
		email.setHostName(config.getConfig(Chaves.EMAIL_SMTP_HOSTNAME));
		
		//Popula lista de destinatários
		for (String endEmail: emails)
			try {
				email.addTo(endEmail, endEmail);
			} catch (EmailException e) {
				throw new RuntimeException("O e-mail de destino " + endEmail + "é inválido",e);
			}

	
		try {
			email.setFrom(config.getConfig(Chaves.EMAIL_ENDERECO_REMETENTE),config.getConfig(Chaves.EMAIL_NOME_REMETENTE))
			.setSubject("Alerta de monitoramento de temperatura e umidade")
			.setCharset("utf-8");
		} catch (EmailException e) {
			throw new RuntimeException("As configurações do remetente são inválidas",e);
		}

		email.setAuthentication(config.getConfig(Chaves.EMAIL_SMTP_USER),config.getConfig(Chaves.EMAIL_SMTP_SENHA));
		email.setStartTLSEnabled(true);
		email.setSSL(true);

		email.setSmtpPort(config.getConfigInteger(Chaves.EMAIL_SMTP_PORTA));
		try {
			ManagedThreadFactory managedThreadFactory = InitialContext.doLookup("java:comp/DefaultManagedThreadFactory");
			
			managedThreadFactory.newThread(() -> { 
				try {
					email
					.setHtmlMsg("Alerta de temperatura e umidade")
					.send();
				} catch (EmailException e) {
					errosDao.salvar(new LogErrosSistema(LocalDateTime.now(), e));
				}
			}).start();
			
		} catch (NamingException e) {
			throw new RuntimeException("Não foi possível inicializar a thread de email",e);
		}
	}
}
