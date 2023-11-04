package br.univesp.sensores.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.univesp.sensores.dto.queryparams.DtParams;
import br.univesp.sensores.dto.queryparams.PaginacaoQueryParams;
import br.univesp.sensores.dto.responses.ListaAlertasResp;
import br.univesp.sensores.entidades.Alerta;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@Stateless
public class AlertaDao {
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Salva a entidade e retorna o ID auto gerado
	 * @param sensor
	 * @return id gerado no banco de dados
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public Long salvar(Alerta alerta) {
		em.persist(alerta);
		em.flush();
		return alerta.getIdAlerta();
	}
	
	public List<ListaAlertasResp> listar(PaginacaoQueryParams paginacao, DtParams dtParams) {
		String jpql = """
				select new br.univesp.sensores.dto.responses.ListaAlertasResp (
					a.idAlerta,a.isHabilitado,a.tipoAlerta,
					a.intervaloEsperaSegundos,a.vlMax,a.vlMin,a.dtCriado
				) from Alerta a
				WHERE 1 = 1 
				""";
		final String orderBy = " order by m.dtMedicao desc ";
		Map<String,Object> params = new HashMap<>();
		
		if (dtParams != null) {
			
			if (dtParams.getDtInicial() != null) {
				jpql += " AND dtCriado >= :dtInicial ";
				params.put("dtCriado", dtParams.getDtInicial());
			}
			
			if (dtParams.getDtFinal() != null) {
				jpql += " AND dtCriado <= :dtFinal ";
				params.put("dtCriado", dtParams.getDtFinal());
			}
			
		}
		
		jpql += orderBy;
		TypedQuery<ListaAlertasResp> query = em.createQuery(jpql, ListaAlertasResp.class);
		params.forEach(query::setParameter);
		
		return paginacao.configurarPaginacao(query).getResultList();
				
	}
}
