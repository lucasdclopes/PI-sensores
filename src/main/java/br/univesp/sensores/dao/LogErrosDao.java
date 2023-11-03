package br.univesp.sensores.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.univesp.sensores.dto.queryparams.DtParams;
import br.univesp.sensores.dto.queryparams.PaginacaoQueryParams;
import br.univesp.sensores.dto.responses.ListaLogErroResp;
import br.univesp.sensores.entidades.LogErrosSistema;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@Stateless
public class LogErrosDao {
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Salva a entidade e retorna o ID auto gerado
	 * @param sensor
	 * @return id gerado no banco de dados
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public Long salvar(LogErrosSistema log) {
		em.persist(log);
		em.flush();
		return log.getIdLogErros();
	}
	
	public List<ListaLogErroResp> listar(PaginacaoQueryParams paginacao, DtParams dtParams) {
		String jpql = """
				select new br.univesp.sensores.dto.responses.ListaLogErro (
					l.idLogErros,l.msgErro,l.dtLog,l.stacktrace
				) from LogErrosSistema l
				WHERE 1 = 1 
				""";
		final String orderBy = " order by l.dtLog desc ";
		Map<String,Object> params = new HashMap<>();
		
		if (dtParams != null) {
			
			if (dtParams.getDtInicial() != null) {
				jpql += " AND dtLog >= :dtInicial ";
				params.put("dtLog", dtParams.getDtInicial());
			}
			
			if (dtParams.getDtFinal() != null) {
				jpql += " AND dtLog <= :dtFinal ";
				params.put("dtLog", dtParams.getDtFinal());
			}
			
		}
		
		jpql += orderBy;
		TypedQuery<ListaLogErroResp> query = em.createQuery(jpql, ListaLogErroResp.class);
		params.forEach(query::setParameter);
		
		return paginacao.configurarPaginacao(query).getResultList();
				
	}
}