package br.univesp.sensores.helpers;

import java.util.Map;

import br.univesp.sensores.dto.queryparams.DtParams;

public class DaoHelper {

	/**
	 * Monta os parâmetros de WHERE para querys que tem range de busca por data
	 * @param params objeto com os parâmetros
	 * @param dtParams parâmetros de data
	 * @param nomeCampo nome do campo de data da entidade
	 * @return
	 */
	public static String addWhereRangeData(
			Map<String,Object> params, final DtParams dtParams, final String nomeCampo) {
		
		String paramsWhere = "";
		if (dtParams != null) {
						
			if (dtParams.getDtInicial() != null) {
				paramsWhere += " AND " + nomeCampo + " >= :dtInicial ";
				params.put("dtInicial", dtParams.getDtInicial());
			}
			
			if (dtParams.getDtFinal() != null) {
				paramsWhere += " AND " + nomeCampo + " <= :dtFinal ";
				params.put("dtFinal", dtParams.getDtFinal());
			}
			
		}
		
		return paramsWhere;
		
	}
}
