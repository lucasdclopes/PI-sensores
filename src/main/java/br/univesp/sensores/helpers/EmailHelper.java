package br.univesp.sensores.helpers;

import java.time.LocalDateTime;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import br.univesp.sensores.dao.LogErrosDao;
import br.univesp.sensores.entidades.LogErrosSistema;
import br.univesp.sensores.helpers.ConfigHelper.Chaves;
import jakarta.ejb.Stateless;
import jakarta.enterprise.concurrent.ManagedThreadFactory;
import jakarta.inject.Inject;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

@Stateless
public class EmailHelper {
	
	@Inject private LogErrosDao errosDao;
	
	private final static String body_email = """
<head>
	<style type=text/css>
	table {
	  border-collapse: collapse;
	}
	
	td, th {
	  border: 1px solid #dddddd;
	  text-align: left;
	  padding: 8px;
	}
	
	tr:nth-child(even) {
	  background-color: #dddddd;
	}
	
	.tabelasdiv {
		margin: 0 auto;
		display: table;
	}
	
	.avisosdiv {
		border: 1px solid red;
		display: table;
	
	}
	.stacktracediv {
		display: table;
		background-color: #dddddd;
		margin: 0 auto;
	}
	</style>
	</head>
	
	<body>
	<div class="tabelasdiv">
		<table>
		  <caption><h3>Informações gerais</h3></caption>
		  <tr>
		    <th>Mensagem retornada ao usuário</th>
		    <td>${msgUsuario}</td>
		  </tr>
		  <tr>
		    <th>Módulo</th>
		    <td>${modulo}</td>
		  </tr>
		   <tr>
		    <th>Hora da montagem do email</th>
		    <td>${timeStamp}</td>
		  </tr>
		   <tr>
		    <th>Versão do Sistema</th>
		    <td>${sistemaVersao}</td>
		  </tr>
		</table>
	
		<table>
		  <caption><h3>Dados do usuário</h3></caption>
		  <tr>
		    <th>Id</th>
		    <td>${idUsuario}</td>
		  </tr>
		  <tr>
		    <th>Operação</th>
		    <td>${idUsuarioOperacao}</td>
		  </tr>
		   <tr>
		    <th>E-mail</th>
		    <td>${usuarioEmail}</td>
		  </tr>
		   <tr>
		    <th>Nome</th>
		    <td>${usuarioNome}</td>
		  </tr>
		</table>
	
		<br><br> 
		<table>
		  <caption><h3>Outros dados da request / response</h3>(valores null se não existir)</caption>
		  <tr>
		    <th>Request body</th>
		    <td>${requestJson}</td>
		  </tr>
		  <tr>
		    <th>Url params</th>
		    <td>${requestQueryParams}</td>
		  </tr>
		   <tr>
		    <th>Mensagem especial</th>
		    <td>${msgEspecial}</td>
		  </tr>
		   <tr>
		    <th>Código de retorno</th>
		    <td>${responseStatus}</td>
		  </tr>
		</table>
	</div>
	<br><br> 
	<div class="avisosdiv">
		<h3>Avisos!</h3>
		<p><b>Este e-mail contém um erro de sistema que necessita de urgente atenção</b> e, provavelmente, atuação imediata! Se tiver dúvidas, por favor contate o responsável para saber como atuar
		</p>
		<p>
		Este e-mail pode conter informações sensíveis ou sigilosas, <b>não compartilhe nenhuma de suas informações</b> com quem não estiver na lista de destinatários e, 
		 principalmente, com pessoas de fora da organização.
		</p>
	</div>
	<br>
	<div class="stacktracediv">
		<h3>stack trace</h3>
		${stackTrace}           
	</div>
</body>
			""";

	public void enviarEmail(String emails) {
		
		ConfigHelper config = ConfigHelper.getInstance();
		
		Properties prop = new Properties();
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.host", config.getConfig(Chaves.EMAIL_SMTP_HOSTNAME));
		prop.put("mail.smtp.port", config.getConfigInteger(Chaves.EMAIL_SMTP_PORTA));
		prop.put("mail.smtp.ssl.trust", config.getConfig(Chaves.EMAIL_SMTP_HOSTNAME));
		

		Session session = Session.getInstance(prop, new Authenticator() {
		    @Override
		    protected PasswordAuthentication getPasswordAuthentication() {
		        return new PasswordAuthentication(config.getConfig(Chaves.EMAIL_SMTP_USER),config.getConfig(Chaves.EMAIL_SMTP_SENHA));
		    }
		});
		
		
		Message message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(config.getConfig(Chaves.EMAIL_ENDERECO_REMETENTE)));
			message.setRecipients(	 
					Message.RecipientType.TO,InternetAddress.parse("to@gmail.com")		  
					);
			message.setSubject("Alerta de monitoramento de temperatura e umidade");
	
			String msg = body_email;
			
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(msg, "text/html; charset=utf-8");
			
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);
	
			message.setContent(multipart);
		} catch (MessagingException e) {
			throw new RuntimeException("Erro durante a configuração do email",e);
		}
		
		try {
			ManagedThreadFactory managedThreadFactory = InitialContext.doLookup("java:comp/DefaultManagedThreadFactory");
			
			managedThreadFactory.newThread(() -> { 
				try {
					Transport.send(message);
				} catch (Exception e) {
					errosDao.salvar(new LogErrosSistema(LocalDateTime.now(), e));
				}
			}).start();
			
		} catch (NamingException e) {
			throw new RuntimeException("Não foi possível inicializar a thread de email",e);
		}
	}
}
