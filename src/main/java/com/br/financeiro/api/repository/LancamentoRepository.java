package com.br.financeiro.api.repository;

import java.time.LocalDate;

import com.br.financeiro.api.model.Lancamento;
import com.br.financeiro.api.repository.lancamento.LancamentoRepositoryQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * O Extends LancamentoRepositoryQuery é apenas para exemplo que pode ser usado com Criteria
 * Porém o método mais "prático" que estamos usando é o que contém @Query
 */
public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery
{
	@Query(value = "FROM Lancamento lancamento "
			+ "WHERE (LOWER(lancamento.descricao) LIKE '%' || LOWER(CAST(:descricao AS string))  || '%' OR :descricao IS NULL) "
			+ "AND (lancamento.dataVencimento >= :dataInicial OR CAST(:dataInicial AS timestamp) IS NULL) "
			+ "AND (lancamento.dataVencimento <= :dataFinal OR CAST(:dataFinal AS timestamp) IS NULL)")
	public Page<Lancamento> listByFilters( @Param("descricao") String descricao,
										   @Param("dataInicial") LocalDate dataInicial,
										   @Param("dataFinal") LocalDate dataFinal,
										   Pageable pageable);

}