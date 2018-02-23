package com.br.financeiro.api.service;

import com.br.financeiro.api.model.Lancamento;
import com.br.financeiro.api.model.Pessoa;
import com.br.financeiro.api.repository.LancamentoRepository;
import com.br.financeiro.api.repository.PessoaRepository;
import com.br.financeiro.api.service.exception.PessoaInexistenteOuInativaException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class LancamentoService
{
	/**
	 *
	 */
	@Autowired
	private PessoaRepository pessoaRepository;

	/**
	 *
	 */
	@Autowired
	private LancamentoRepository lancamentoRepository;

	/**
	 *
	 */
	@Autowired
	private MessageSource messageSource;

	/**
	 *
	 */
	public Lancamento salvar( Lancamento lancamento )
	{
		final Pessoa pessoa = this.pessoaRepository.findOne( lancamento.getPessoa().getId() );
		if ( pessoa == null || pessoa.isInativo() )
		{
			throw new PessoaInexistenteOuInativaException();
		}

		return this.lancamentoRepository.save( lancamento );
	}

	/**
	 *
	 */
	public Lancamento atualizar( Long id, Lancamento lancamento )
	{
		final Lancamento lancamentoSalvo = this.findLancamentoById( id );

		if ( !lancamento.getPessoa().equals( lancamentoSalvo.getPessoa() ) )
		{
			this.validarPessoa( lancamento );
		}

		BeanUtils.copyProperties( lancamento, lancamentoSalvo, "id" );

		return this.lancamentoRepository.save( lancamentoSalvo );
	}

	/**
	 *
	 */
	private void validarPessoa( Lancamento lancamento )
	{
		Pessoa pessoa = null;

		if ( lancamento.getPessoa().getId() != null )
		{
			pessoa = pessoaRepository.findOne( lancamento.getPessoa().getId() );
		}

		if ( pessoa == null || pessoa.isInativo() )
		{
			throw new PessoaInexistenteOuInativaException();
		}
	}

	/**
	 *
	 */
	private Lancamento findLancamentoById( Long id )
	{
		final Lancamento lancamentoSalvo = this.lancamentoRepository.findOne( id );

		Assert.notNull( lancamentoSalvo,
				this.messageSource.getMessage( "notFoundById", new Object[]{id}, LocaleContextHolder.getLocale() ) );

//		if ( lancamentoSalvo == null )
//		{
//			throw new IllegalArgumentException();
//		}

		return lancamentoSalvo;
	}
}