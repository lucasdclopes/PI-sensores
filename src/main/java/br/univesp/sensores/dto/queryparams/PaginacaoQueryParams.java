package br.univesp.sensores.dto.queryparams;

import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.QueryParam;

public class PaginacaoQueryParams {
	
	private Integer MAX_ITENS = 100;
	
	@QueryParam("size") 
	protected Integer nroLinhas;
	@QueryParam("page") 
	protected Integer nroPagina;
	
	
	public Integer getNroLinhas(){
		if (nroLinhas == null || nroLinhas < 1)
			nroLinhas = 20;
		if (nroLinhas > MAX_ITENS)
			nroLinhas = MAX_ITENS;
		return nroLinhas;
	}
	/**
	 * Valida o número da página Se for null ou 0, retorna o padrão definido na propriedade padraoNroPagina
	 * @return valor do número da página
	 */
	public Integer getNroPagina(){
		if (nroPagina == null || nroPagina < 1)
			this.nroPagina = 1;
		return this.nroPagina;
	}
	
	/**
	 * Auxiliar a paginação na JPA.
	 */
	public <T> TypedQuery<T> configurarPaginacao(TypedQuery<T> typedQuery) {
		return typedQuery
		.setFirstResult(this.getNroLinhas() * (this.getNroPagina() - 1))
		.setMaxResults(nroLinhas);
	}
}
