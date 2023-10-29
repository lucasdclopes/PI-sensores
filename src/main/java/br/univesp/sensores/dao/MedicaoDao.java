package br.univesp.sensores.dao;

import java.util.List;

import br.univesp.sensores.dto.queryparams.PaginacaoQueryParams;
import br.univesp.sensores.dto.responses.ListaMedicoes;
import br.univesp.sensores.entidades.MedicaoSensor;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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
	
	public List<ListaMedicoes> listar(PaginacaoQueryParams paginacao) {
		String jpql = """
				select new br.univesp.sensores.dto.responses.ListaMedicoes (
					m.idMedicao,m.vlTemperatura,m.vlUmidade,m.dtMedicao
				) from MedicaoSensor m
				order by m.dtMedicao
				""";
		
		return paginacao.configurarPaginacao(
				em.createQuery(jpql, ListaMedicoes.class)
				).getResultList();
				
	}
}
