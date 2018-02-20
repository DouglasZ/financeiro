package com.br.financeiro.api.repository.lancamento;

import com.br.financeiro.api.model.Lancamento;
import com.br.financeiro.api.repository.filter.LancamentoFilter;
import com.br.financeiro.api.repository.projection.ResumoLancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Exemplo de como implementar a Query na "mão"
 */
public interface LancamentoRepositoryQuery
{
	/**
	 *
	 */
	public Page<Lancamento> filtrar( LancamentoFilter lancamentoFilter, Pageable pageable );

	/**
	 *
	 */
	public Page<ResumoLancamento> resumir( LancamentoFilter lancamentoFilter, Pageable pageable );

}