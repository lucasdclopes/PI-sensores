package br.univesp.sensores.resources;

import br.univesp.sensores.dao.MedicaoDao;
import br.univesp.sensores.dto.queryparams.PaginacaoQueryParams;
import br.univesp.sensores.dto.requests.NovaMedicao;
import br.univesp.sensores.entidades.MedicaoSensor;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/medicao")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Medicao {
	
	@Inject MedicaoDao medicaoDao;
	
	@GET
	public Response getSensores(@Valid @BeanParam final PaginacaoQueryParams paginacao) {
		
		return null;
	}
	
	@POST
	public Response salvarMedicao(NovaMedicao novaMedicao) {
		
		MedicaoSensor med = new MedicaoSensor(novaMedicao.vlTemperatura(), novaMedicao.vlUmidade());
		Integer id = medicaoDao.salvarMedicao(med);
		
		return Response.status(Status.CREATED).header("created", "medicao/" + id).build();
		
	}

}
