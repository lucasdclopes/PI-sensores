package br.univesp.sensores.resources;

import java.math.BigDecimal;
import java.util.Random;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import br.univesp.sensores.dao.MedicaoDao;
import br.univesp.sensores.entidades.MedicaoSensor;
import jakarta.enterprise.concurrent.ManagedThreadFactory;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/simulador")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Simulador {
	
	@Inject private MedicaoDao medicaoDao;
	
	public static Boolean executar = false;
	
	@POST
	@Path("/start")
	public Response start() throws NamingException {
		
		if (executar)
			return Response.status(Status.CONFLICT).entity("Simulador já estava iniciado").build();
		
		executar = true;
		ManagedThreadFactory managedThreadFactory = InitialContext.doLookup("java:comp/DefaultManagedThreadFactory");
		managedThreadFactory.newThread(() -> { 
			final Integer MIN_TEMP = 15;
			final Integer MIN_UMID = 5;
			
			final Integer MAX_TEMP = 50;
			final Integer MAX_UMID = 70;
			Random rnd = new Random();
			Double temperatura = rnd.nextDouble(MIN_TEMP, MAX_TEMP);
			Double umidade = rnd.nextDouble(MIN_UMID, MAX_UMID);
			while (Simulador.executar) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException("Erro executando o sleep da thread",e);
				}
				medicaoDao.salvarMedicao(new MedicaoSensor(
						BigDecimal.valueOf(temperatura), BigDecimal.valueOf(umidade))
						);
				
				//gera aleatórios não muito distantes dos valores anteriores
				Double nextTemp = rnd.nextDouble(-5, 5);
				if (nextTemp + temperatura < MIN_TEMP || nextTemp + temperatura > MAX_TEMP)
					nextTemp = nextTemp * -1;
					
				Double nextUmid = rnd.nextDouble(-1, 1);
				if (nextUmid + umidade < MIN_UMID || nextUmid + umidade > MAX_UMID)
					nextUmid = nextUmid * -1;
					
				temperatura += nextTemp;
				umidade += nextUmid;
				
			}
		}).start();
		
		return Response.ok().build();
		
	}
	
	
	@POST
	@Path("/stop")
	public Response stop() {
		executar = false;
		return Response.ok().build();
	}

}