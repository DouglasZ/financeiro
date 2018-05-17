package com.br.financeiro.api.repository.lancamento;

import java.time.LocalDate;
import java.util.List;

import com.br.financeiro.api.dto.LancamentoEstatisticaCategoria;
import com.br.financeiro.api.dto.LancamentoEstatisticaDia;
import com.br.financeiro.api.dto.LancamentoEstatisticaPessoa;
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
	Page<Lancamento> filtrar( LancamentoFilter lancamentoFilter, Pageable pageable );

	/**
	 *
	 */
	Page<ResumoLancamento> resumir( LancamentoFilter lancamentoFilter, Pageable pageable );

	/**
	 *
	 */
	List<LancamentoEstatisticaCategoria> porCategoria( LocalDate mesReferencia );

	/**
	 *
	 */
	List<LancamentoEstatisticaDia> porDia( LocalDate mesReferencia );

	/**
	 *
	 */
	List<LancamentoEstatisticaPessoa> porPessoa( LocalDate inicio, LocalDate fim );
}