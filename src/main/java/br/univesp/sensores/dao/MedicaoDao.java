package br.univesp.sensores.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.univesp.sensores.dto.queryparams.DtParams;
import br.univesp.sensores.dto.queryparams.PaginacaoQueryParams;
import br.univesp.sensores.dto.responses.MedicaoItemResp;
import br.univesp.sensores.dto.responses.MedicaoListaResp;
import br.univesp.sensores.entidades.MedicaoSensor;
import br.univesp.sensores.helpers.DaoHelper;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@Stateless
public class MedicaoDao {
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Salva a entidade e retorna o ID auto gerado
	 * @param sensor
	 * @return id gerado no banco de dados
	 */
	@Transactional(value = TxType.REQUIRES_NEW)
	public Long salvarMedicao(MedicaoSensor sensor) {
		em.persist(sensor);
		em.flush();
		return sensor.getIdMedicao();
	}
	
	public MedicaoListaResp listar(final PaginacaoQueryParams paginacao, final DtParams dtParams, boolean tempoReal) {
		
		Long total = 0L;
		String where = "WHERE 1 = 1 ";
		String jpql = """
				select new br.univesp.sensores.dto.responses.MedicaoItemResp (
					m.idMedicao,m.vlTemperatura,m.vlUmidade,m.dtMedicao
				) from MedicaoSensor m 
				""";
		final String orderBy = " order by m.dtMedicao desc ";
		Map<String,Object> params = new HashMap<>();
		
		where += DaoHelper.addWhereRangeData(params, dtParams, "dtMedicao");
		
		jpql += where + orderBy;
		TypedQuery<MedicaoItemResp> query = em.createQuery(jpql, MedicaoItemResp.class);
		params.forEach(query::setParameter);
		
		if (!tempoReal) { //monitoramento de tempo real não deve utilizar esta informação, além de custar muito desempenho	
			String jpqlCount = """
				select count(m.idMedicao) from MedicaoSensor m 
				""" + where;
			
			TypedQuery<Long> queryCount = em.createQuery(jpqlCount, Long.class);
			params.forEach(queryCount::setParameter);
			total = queryCount.getSingleResult();
		}
		List<MedicaoItemResp> resultList = paginacao.configurarPaginacao(query).getResultList();
		return new MedicaoListaResp(
				DaoHelper.infoPaginas(paginacao, total, resultList.size()),
				resultList
				);
				
	}
}
