package br.univesp.sensores.resources;

import java.util.List;

import br.univesp.sensores.dao.MedicaoDao;
import br.univesp.sensores.dto.queryparams.DtParams;
import br.univesp.sensores.dto.queryparams.PaginacaoQueryParams;
import br.univesp.sensores.dto.requests.NovaMedicao;
import br.univesp.sensores.dto.responses.ListaMedicoesResp;
import br.univesp.sensores.entidades.MedicaoSensor;
import br.univesp.sensores.helpers.ConfigHelper;
import br.univesp.sensores.helpers.ConfigHelper.Chaves;
import br.univesp.sensores.helpers.ResourceHelper;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.ResponseBuilder;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;

@Path("/medicao")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MedicaoResource {
	
	@Inject MedicaoDao medicaoDao;
	
	@GET
	public Response getSensores(@Valid @BeanParam final PaginacaoQueryParams paginacao, @Valid @BeanParam final DtParams dtParams ) {
		
		List<ListaMedicoesResp> lista = medicaoDao.listar(paginacao,dtParams);
		if (lista.isEmpty())
			return Response.status(Status.NO_CONTENT).build();
		
		return Response.ok().entity(lista).build();
	}
	
	@POST
	public Response salvarMedicao(final NovaMedicao novaMedicao, @Context UriInfo uriInfo, @QueryParam("showIntervalo") final boolean showIntervalo) {
		
		MedicaoSensor med = new MedicaoSensor(novaMedicao.vlTemperatura(), novaMedicao.vlUmidade());
		Long id = medicaoDao.salvarMedicao(med);
		
		ResponseBuilder builder = Response.created(ResourceHelper.montarLocation(uriInfo,id));
		if (showIntervalo) 
			builder = builder.entity(ConfigHelper.getInstance().getConfigInteger(Chaves.MONITORAMENTO_INTERVALO));
			
		return builder.build();
		
	}


}
