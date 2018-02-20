package com.br.financeiro.api.service;

import com.br.financeiro.api.model.Lancamento;
import com.br.financeiro.api.model.Pessoa;
import com.br.financeiro.api.repository.LancamentoRepository;
import com.br.financeiro.api.repository.PessoaRepository;
import com.br.financeiro.api.service.exception.PessoaInexistenteOuInativaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	public Lancamento salvar( Lancamento lancamento )
	{
		final Pessoa pessoa = this.pessoaRepository.findOne( lancamento.getPessoa().getId() );
		if ( pessoa == null || pessoa.isInativo() )
		{
			throw new PessoaInexistenteOuInativaException();
		}

		return this.lancamentoRepository.save( lancamento );
	}
}