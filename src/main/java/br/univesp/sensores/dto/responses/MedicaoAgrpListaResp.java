package br.univesp.sensores.dto.responses;

import java.util.List;

import br.univesp.sensores.helpers.DaoHelper.Page;

public record MedicaoAgrpListaResp(
		Page page,
		List<MedicaoAgrpItemResp> medicoes
		) {

}
