package br.univesp.sensores.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.univesp.sensores.dto.queryparams.DtParams;
import br.univesp.sensores.dto.queryparams.PaginacaoQueryParams;
import br.univesp.sensores.dto.responses.ListaMedicoes;
import br.univesp.sensores.entidades.MedicaoSensor;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Stateless
public class MedicaoDao {
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Salva a entidade e retorna o ID auto gerado
	 * @param sensor
	 * @return id gerado no banco de dados
	 */
	public Long salvarMedicao(MedicaoSensor sensor) {
		em.persist(sensor);
		em.flush();
		return sensor.getIdMedicao();
	}
	
	public List<ListaMedicoes> listar(PaginacaoQueryParams paginacao, DtParams dtParams) {
		String jpql = """
				select new br.univesp.sensores.dto.responses.ListaMedicoes (
					m.idMedicao,m.vlTemperatura,m.vlUmidade,m.dtMedicao
				) from MedicaoSensor m
				WHERE 1 = 1 
				""";
		final String orderBy = " order by m.dtMedicao ";
		Map<String,Object> params = new HashMap<>();
		
		if (dtParams != null) {
			
			if (dtParams.getDtInicial() != null) {
				jpql += " AND dtMedicao >= :dtInicial ";
				params.put("dtInicial", dtParams.getDtInicial());
			}
			
			if (dtParams.getDtFinal() != null) {
				jpql += " AND dtMedicao <= :dtFinal ";
				params.put("dtFinal", dtParams.getDtFinal());
			}
			
		}
		
		jpql += orderBy;
		TypedQuery<ListaMedicoes> query = em.createQuery(jpql, ListaMedicoes.class);
		params.forEach(query::setParameter);
		
		return paginacao.configurarPaginacao(query).getResultList();
				
	}
}
