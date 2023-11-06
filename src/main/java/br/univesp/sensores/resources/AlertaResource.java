package br.univesp.sensores.resources;

import java.util.List;

import br.univesp.sensores.dao.AlertaDao;
import br.univesp.sensores.dto.queryparams.DtParams;
import br.univesp.sensores.dto.queryparams.PaginacaoQueryParams;
import br.univesp.sensores.dto.requests.NovoAlerta;
import br.univesp.sensores.dto.responses.ListaAlertasResp;
import br.univesp.sensores.entidades.Alerta;
import br.univesp.sensores.entidades.Alerta.TipoAlerta;
import br.univesp.sensores.helpers.EnumHelper;
import br.univesp.sensores.helpers.ResourceHelper;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;

@Path("/alerta")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AlertaResource {
	
	@Inject AlertaDao alertaDao;
	
	@GET
	public Response getAlertas(@Valid @BeanParam final PaginacaoQueryParams paginacao, @Valid @BeanParam final DtParams dtParams ) {
		
		List<ListaAlertasResp> lista = alertaDao.listar(paginacao,dtParams);
		if (lista.isEmpty())
			return Response.status(Status.NO_CONTENT).build();
		
		return Response.ok().entity(lista).build();
	}
	
	@GET
	@Path("/{idAlerta}")
	public Response getAlertasEnviados(@PathParam("idAlerta") final Long idAlerta, 
			@Valid @BeanParam final PaginacaoQueryParams paginacao, @Valid @BeanParam final DtParams dtParams ) {
		
		List<ListaAlertasResp> lista = alertaDao.listar(paginacao,dtParams);
		if (lista.isEmpty())
			return Response.status(Status.NO_CONTENT).build();
		
		return Response.ok().entity(lista).build();
	}
	
	@POST
	public Response salvarNovoAlerta(final NovoAlerta novoAlerta, @Context UriInfo uriInfo) {
		
		TipoAlerta tipoAlerta = EnumHelper.getEnumFromCodigo(novoAlerta.tipoAlerta(),TipoAlerta.class);
		
		Alerta alerta = new Alerta(
				tipoAlerta, novoAlerta.intervaloEsperaSegundos(), novoAlerta.vlMax(), novoAlerta.vlMin(),novoAlerta.destinatarios()
				);
		
		Long id = alertaDao.salvar(alerta);
		return Response
				.created(ResourceHelper.montarLocation(uriInfo,id))
				.build();	
	}
}