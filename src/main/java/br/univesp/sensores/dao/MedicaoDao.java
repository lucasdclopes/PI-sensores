package br.univesp.sensores.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.univesp.sensores.dto.queryparams.DtParams;
import br.univesp.sensores.dto.queryparams.PaginacaoQueryParams;
import br.univesp.sensores.dto.responses.ListaMedicoesResp;
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
	
	public List<ListaMedicoesResp> listar(final PaginacaoQueryParams paginacao, final DtParams dtParams) {
		String jpql = """
				select new br.univesp.sensores.dto.responses.ListaMedicoes (
					m.idMedicao,m.vlTemperatura,m.vlUmidade,m.dtMedicao
				) from MedicaoSensor m
				WHERE 1 = 1 
				""";
		final String orderBy = " order by m.dtMedicao desc ";
		Map<String,Object> params = new HashMap<>();
		
		jpql += DaoHelper.addWhereRangeData(params, dtParams, "dtCriado");
		jpql += orderBy;
		
		TypedQuery<ListaMedicoesResp> query = em.createQuery(jpql, ListaMedicoesResp.class);
		params.forEach(query::setParameter);
		
		return paginacao.configurarPaginacao(query).getResultList();
				
	}
}
