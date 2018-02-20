package com.br.financeiro.api.resource;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import com.br.financeiro.api.event.RecursoCriadoEvent;
import com.br.financeiro.api.model.Pessoa;
import com.br.financeiro.api.repository.PessoaRepository;
import com.br.financeiro.api.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pessoas")
public class PessoaResource
{
	/*-------------------------------------------------------------------
	 *				 		     ATTRIBUTES
	 *-------------------------------------------------------------------*/
	/**
	 *
	 */
	@Autowired
	private PessoaRepository pessoaRepository;

	/**
	 *
	 */
	@Autowired
	private PessoaService pessoaService;

	/**
	 * Publicador de eventos de aplicação
	 */
	@Autowired
	private ApplicationEventPublisher publisher;

	/*-------------------------------------------------------------------
 	 *							BEHAVIORS
 	 *-------------------------------------------------------------------*/

	/**
	 *
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")
	public ResponseEntity<Pessoa> criar( @Valid @RequestBody Pessoa pessoa, HttpServletResponse response )
	{
		final Pessoa pessoaSalva = this.pessoaRepository.save( pessoa );

		this.publisher.publishEvent( new RecursoCriadoEvent( this, response, pessoaSalva.getId() ) );

		return ResponseEntity.status( HttpStatus.CREATED ).body( pessoaSalva );
	}

	/**
	 *
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_PESSOA') and #oauth2.hasScope('read')")
	public ResponseEntity<Pessoa> findPessoaById( @PathVariable Long id )
	{
		final Pessoa pessoa = this.pessoaRepository.findOne( id );
		return pessoa != null ? ResponseEntity.ok( pessoa ) : ResponseEntity.notFound().build();
	}

	/**
	 *
	 */
	@DeleteMapping("/{id}")
	// Deu OK porem devemos dizer que não tem nenhum conteúdo para retornar
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_REMOVER_PESSOA') and #oauth2.hasScope('write')")
	public void remover( @PathVariable Long id )
	{
		this.pessoaRepository.delete( id );
	}

	/**
	 *
	 */
	@PutMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")
	public ResponseEntity<Pessoa> atualizar( @PathVariable Long id, @Valid @RequestBody Pessoa pessoa )
	{
		final Pessoa pessoaSalva = this.pessoaService.atualizar( id, pessoa );

		return ResponseEntity.ok( pessoaSalva );
	}

	/**
	 *
	 */
	@PutMapping("/{id}/ativo")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_PESSOA') and #oauth2.hasScope('write')")
	public void atualizarPropriedadeAtivo( @PathVariable Long id, @RequestBody Boolean ativo )
	{
		this.pessoaService.atualizarPropriedadeAtivo( id, ativo );
	}

}