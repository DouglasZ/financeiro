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
	public Pessoa salvar( Pessoa pessoa )
	{
		// Devido ao @JsonIgnoreProperties para evitar o StackOverflow devemos setar a pessoa em contato ao salvar
		pessoa.getContatos().forEach( contato -> contato.setPessoa( pessoa ) );

		return this.pessoaRepository.save( pessoa );
	}

	/**
	 *
	 */
	public Pessoa atualizar( Long id, Pessoa pessoa )
	{
		final Pessoa pessoaSalva = this.buscarPessoaById( id );

		// Quando o contato é removido de pessoa e a lista de contato fica vazia ela deixa de ser uma Lista Persistente
		// Por isso devemos trabalhar com a lista de pessoaSalva pois como foi retornado do banco ela ainda é uma lista Persistente
		pessoaSalva.getContatos().clear(); // Apenas limpamos os dados da lista e não criamos uma nova instancia pra não perder a lista persistente
		pessoaSalva.getContatos().addAll( pessoa.getContatos() ); // Setamos a lista de contatos que veio por parametro na lista persistente
		pessoaSalva.getContatos().forEach( contato -> contato.setPessoa( pessoaSalva ) );

		//Faz a cópia do obj pessoa para o obj pessoaSalva que veio do banco ignorando apenas o id e o contatos
		BeanUtils.copyProperties( pessoa, pessoaSalva, "id", "contatos" );

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
