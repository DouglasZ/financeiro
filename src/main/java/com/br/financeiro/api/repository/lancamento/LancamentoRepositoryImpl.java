package com.br.financeiro.api.repository.lancamento;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.br.financeiro.api.dto.LancamentoEstatisticaCategoria;
import com.br.financeiro.api.dto.LancamentoEstatisticaDia;
import com.br.financeiro.api.dto.LancamentoEstatisticaPessoa;
import com.br.financeiro.api.model.Lancamento;
import com.br.financeiro.api.repository.filter.LancamentoFilter;
import com.br.financeiro.api.repository.projection.ResumoLancamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;


public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery
{
	/**
	 *
	 */
	@PersistenceContext
	private EntityManager manager;

	@Override
	public List<LancamentoEstatisticaPessoa> porPessoa( LocalDate inicio, LocalDate fim )
	{
		CriteriaBuilder criteriaBuilder = this.manager.getCriteriaBuilder();

		CriteriaQuery<LancamentoEstatisticaPessoa> criteriaQuery = criteriaBuilder
				.createQuery( LancamentoEstatisticaPessoa.class );

		Root<Lancamento> root = criteriaQuery.from( Lancamento.class );

		criteriaQuery.select( criteriaBuilder.construct( LancamentoEstatisticaPessoa.class,
				root.get( "tipo" ),
				root.get( "pessoa" ),
				criteriaBuilder.sum( root.get( "valor" ) ) ) );

		criteriaQuery.where(
				criteriaBuilder.greaterThanOrEqualTo( root.get( "dataVencimento" ), inicio ),
				criteriaBuilder.lessThanOrEqualTo( root.get( "dataVencimento" ), fim )
		);

		criteriaQuery.groupBy( root.get( "tipo" ), root.get( "pessoa" ) );

		TypedQuery<LancamentoEstatisticaPessoa> typedQuery = this.manager.createQuery( criteriaQuery );

		return typedQuery.getResultList();
	}

	@Override
	public List<LancamentoEstatisticaDia> porDia( LocalDate mesReferencia )
	{
		CriteriaBuilder criteriaBuilder = this.manager.getCriteriaBuilder();

		CriteriaQuery<LancamentoEstatisticaDia> criteriaQuery = criteriaBuilder
				.createQuery( LancamentoEstatisticaDia.class );

		Root<Lancamento> root = criteriaQuery.from( Lancamento.class );

		criteriaQuery.select( criteriaBuilder.construct( LancamentoEstatisticaDia.class,
				root.get( "tipo" ),
				root.get( "dataVencimento" ),
				criteriaBuilder.sum( root.get( "valor" ) ) ) );

		LocalDate primeiroDia = mesReferencia.withDayOfMonth( 1 );
		LocalDate ultimoDia = mesReferencia.withDayOfMonth( mesReferencia.lengthOfMonth() );

		criteriaQuery.where(
				criteriaBuilder.greaterThanOrEqualTo( root.get( "dataVencimento" ), primeiroDia ),
				criteriaBuilder.lessThanOrEqualTo( root.get( "dataVencimento" ), ultimoDia )
		);

		criteriaQuery.groupBy( root.get( "tipo" ), root.get( "dataVencimento" ) );

		TypedQuery<LancamentoEstatisticaDia> typedQuery = this.manager.createQuery( criteriaQuery );

		return typedQuery.getResultList();
	}

	@Override
	public List<LancamentoEstatisticaCategoria> porCategoria( LocalDate mesReferencia )
	{
		CriteriaBuilder criteriaBuilder = this.manager.getCriteriaBuilder();

		CriteriaQuery<LancamentoEstatisticaCategoria> criteriaQuery = criteriaBuilder
				.createQuery( LancamentoEstatisticaCategoria.class );

		Root<Lancamento> root = criteriaQuery.from( Lancamento.class );

		criteriaQuery.select( criteriaBuilder.construct( LancamentoEstatisticaCategoria.class,
				root.get( "categoria" ),
				criteriaBuilder.sum( root.get( "valor" ) ) ) );

		LocalDate primeiroDia = mesReferencia.withDayOfMonth( 1 );
		LocalDate ultimoDia = mesReferencia.withDayOfMonth( mesReferencia.lengthOfMonth() );

		criteriaQuery.where(
				criteriaBuilder.greaterThanOrEqualTo( root.get( "dataVencimento" ), primeiroDia ),
				criteriaBuilder.lessThanOrEqualTo( root.get( "dataVencimento" ), ultimoDia )
		);

		criteriaQuery.groupBy( root.get( "categoria" ) );

		TypedQuery<LancamentoEstatisticaCategoria> typedQuery = this.manager.createQuery( criteriaQuery );

		return typedQuery.getResultList();
	}

	/**
	 *
	 */
	@Override
	public Page<Lancamento> filtrar( LancamentoFilter lancamentoFilter, Pageable pageable )
	{
		CriteriaBuilder builder = this.manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery( Lancamento.class );
		Root<Lancamento> root = criteria.from( Lancamento.class );

		Predicate[] predicates = criarRestricoes( lancamentoFilter, builder, root );
		criteria.where( predicates );

		TypedQuery<Lancamento> query = this.manager.createQuery( criteria );
		adicionarRestricoesDePaginacao( query, pageable );

		return new PageImpl<>( query.getResultList(), pageable, total( lancamentoFilter ) );
	}

	/**
	 * Para realizar a consulta resumida
	 */
	@Override
	public Page<ResumoLancamento> resumir( LancamentoFilter lancamentoFilter, Pageable pageable )
	{
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<ResumoLancamento> criteria = builder.createQuery( ResumoLancamento.class );
		Root<Lancamento> root = criteria.from( Lancamento.class );

		criteria.select( builder.construct( ResumoLancamento.class
				, root.get( "id" )
				, root.get( "descricao" )
				, root.get( "dataVencimento" )
				, root.get( "dataPagamento" )
				, root.get( "valor" )
				, root.get( "tipo" )
				, root.get( "categoria" ).get( "nome" )
				, root.get( "pessoa" ).get( "nome" ) ) );

		Predicate[] predicates = criarRestricoes( lancamentoFilter, builder, root );
		criteria.where( predicates );

		TypedQuery<ResumoLancamento> query = manager.createQuery( criteria );
		adicionarRestricoesDePaginacao( query, pageable );

		return new PageImpl<>( query.getResultList(), pageable, total( lancamentoFilter ) );
	}

	/**
	 *
	 */
	private Predicate[] criarRestricoes( LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
										 Root<Lancamento> root )
	{
		List<Predicate> predicates = new ArrayList<>();

		if ( !StringUtils.isEmpty( lancamentoFilter.getDescricao() ) )
		{
			// Obs: No lugar de root.get( "descricao" ), poderíamos estar usando o Metamodel. Ficaria root.get( Lancamento_.descricao ).
			// O Metamodel analisa a entidade e vai criar uma classe com atributos estáticos de uma forma que,
			// Ao alterar qualquer atributo dentro da classe o Metamodel irá atualizar os atributos estáticos.
			// Isso ajuda a evitar erros no momento de escrver o root.get().
			predicates.add( builder.like(
					builder.lower( root.get( "descricao" ) ), "%" + lancamentoFilter.getDescricao().toLowerCase() + "%" ) );
		}

		if ( lancamentoFilter.getDataInicial() != null )
		{
			predicates.add(
					builder.greaterThanOrEqualTo( root.get( "dataVencimento" ), lancamentoFilter.getDataInicial() ) );
		}

		if ( lancamentoFilter.getDataFinal() != null )
		{
			predicates.add(
					builder.lessThanOrEqualTo( root.get( "dataVencimento" ), lancamentoFilter.getDataFinal() ) );
		}

		return predicates.toArray( new Predicate[predicates.size()] );
	}

	/**
	 *
	 */
	private void adicionarRestricoesDePaginacao( TypedQuery<?> query, Pageable pageable )
	{
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalRegistrosPorPagina;

		query.setFirstResult( primeiroRegistroDaPagina );
		query.setMaxResults( totalRegistrosPorPagina );
	}

	/**
	 *
	 */
	private Long total( LancamentoFilter lancamentoFilter )
	{
		CriteriaBuilder builder = this.manager.getCriteriaBuilder();
		CriteriaQuery<Long> criteria = builder.createQuery( Long.class );
		Root<Lancamento> root = criteria.from( Lancamento.class );

		Predicate[] predicates = criarRestricoes( lancamentoFilter, builder, root );
		criteria.where( predicates );

		criteria.select( builder.count( root ) );
		return this.manager.createQuery( criteria ).getSingleResult();
	}
}