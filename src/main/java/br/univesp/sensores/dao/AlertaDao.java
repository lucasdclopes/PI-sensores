package br.univesp.sensores.dao;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.univesp.sensores.dto.queryparams.DtParams;
import br.univesp.sensores.dto.queryparams.PaginacaoQueryParams;
import br.univesp.sensores.dto.responses.ListaAlertasResp;
import br.univesp.sensores.entidades.Alerta;
import br.univesp.sensores.helpers.DaoHelper;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
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
	
	public List<ListaAlertasResp> listar(final PaginacaoQueryParams paginacao, final DtParams dtParams) {
		String jpql = """
				select new br.univesp.sensores.dto.responses.ListaAlertasResp (
					a.idAlerta,a.isHabilitado,a.tipoAlerta,a.intervaloEsperaSegundos,
					a.vlMax,a.vlMin,a.dtCriado,a.destinatarios
				) from Alerta a
				WHERE 1 = 1 
				""";
		final String orderBy = " order by m.dtMedicao desc ";
		Map<String,Object> params = new HashMap<>();
		
		jpql += DaoHelper.addWhereRangeData(params, dtParams, "dtCriado");
		jpql += orderBy;
		
		TypedQuery<ListaAlertasResp> query = em.createQuery(jpql, ListaAlertasResp.class);
		params.forEach(query::setParameter);
		
		return paginacao.configurarPaginacao(query).getResultList();
				
	}
	
	public List<LocalDateTime> listarEnviados(final Long idAlerta, final PaginacaoQueryParams paginacao){
		
		String jpql = """
				select e.dtMedicao from AlertaEnviado e 
				where e.alerta.idAlerta = :idAlerta
				order by e.dtMedicao desc
				""";
		
		return  paginacao.configurarPaginacao(
				em.createQuery(jpql, LocalDateTime.class))
				.setParameter("idAlerta", idAlerta)
				.getResultList();
	}
	
	@Transactional(value = TxType.REQUIRED)
	public void deletarEnviados(final Long idAlerta,final DtParams dtParams) {
		String jpql = """
				delete from AlertaEnviado ae 
				where ae.alerta.idAlerta = :idAlerta;
				""";
		
		Map<String,Object> params = new HashMap<>();
		jpql += DaoHelper.addWhereRangeData(params, dtParams, "dtCriado");
		
		Query query = em.createQuery(jpql);
		params.forEach(query::setParameter);
		query.executeUpdate();
	
	}
	
	@Transactional(value = TxType.REQUIRES_NEW)
	public void deletarPorId(final Long idAlerta) {
		
		deletarEnviados(idAlerta,null);		//deleta os relacionamentos
		em.flush();
		
		String jpql = """
				delete from Alerta a 
				where a.idAlerta = :idAlerta;
				""";
		
		em.createQuery(jpql)
		.setParameter("idAlerta", idAlerta)
		.executeUpdate();
		
	}
	
}
