package com.br.financeiro.api.service;

import com.br.financeiro.api.model.Pessoa;
import com.br.financeiro.api.repository.PessoaRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class PessoaService
{
	/**
	 *
	 */
	@Autowired
	private PessoaRepository pessoaRepository;

	/**
	 *
	 */
	public Pessoa atualizar( Long id, Pessoa pessoa )
	{
		final Pessoa pessoaSalva = this.buscarPessoaById( id );

		//Faz a cópia do obj pessoa para o obj pessoaSala que veio do banco ignorando apenas o id
		BeanUtils.copyProperties( pessoa, pessoaSalva, "id" );

		return this.pessoaRepository.save( pessoaSalva );
	}

	/**
	 *
	 */
	public void atualizarPropriedadeAtivo( Long id, Boolean ativo )
	{
		final Pessoa pessoaSalva = this.buscarPessoaById( id );
		pessoaSalva.setAtivo( ativo );

		this.pessoaRepository.save( pessoaSalva );
	}

	/**
	 *
	 */
	public Pessoa buscarPessoaById( Long id )
	{
		final Pessoa pessoaSalva = this.pessoaRepository.findOne( id );

		//Validamos se o ID não for encontrado e apresentamos uma exceção
		if ( pessoaSalva == null )
		{
			//Esperava pelo menos 1 elemente e foi encontrado 0
			throw new EmptyResultDataAccessException( 1 );
		}
		return pessoaSalva;
	}
}
