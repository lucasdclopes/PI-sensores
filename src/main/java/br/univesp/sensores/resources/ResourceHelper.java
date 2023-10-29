package br.univesp.sensores.resources;

import java.net.URI;

import jakarta.ws.rs.core.UriInfo;

public class ResourceHelper {

	public static URI montarLocation(UriInfo uriInfo, Long id) {
        return uriInfo.getAbsolutePathBuilder()
        		.path(Long.toString(id))
        		.build();
	}
}
