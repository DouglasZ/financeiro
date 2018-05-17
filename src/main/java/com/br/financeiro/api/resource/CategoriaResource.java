package com.br.financeiro.api.resource;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.br.financeiro.api.event.RecursoCriadoEvent;
import com.br.financeiro.api.model.Categoria;
import com.br.financeiro.api.repository.CategoriaRepository;

@RestController
@RequestMapping("/categorias")
public class CategoriaResource
{
	/*-------------------------------------------------------------------
	 *				 		     ATTRIBUTES
	 *-------------------------------------------------------------------*/
	/**
	 *
	 */
	@Autowired
	private CategoriaRepository categoriaRepository;

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
	@GetMapping
	@PreAuthorize("hasAnyAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')")
	public List<Categoria> listar()
	{
		return this.categoriaRepository.findAll();
	}

	/**
	 *
	 */
	@PostMapping
	@PreAuthorize("hasAnyAuthority('ROLE_CADASTRAR_CATEGORIA') and #oauth2.hasScope('write')")
	//@ResponseStatus(HttpStatus.CREATED) Não precisa, pois já está definido no return
	//Para validar os campos do objeto é precisar unsar o @Valid
	public ResponseEntity<Categoria> criar( @Valid @RequestBody Categoria categoria, HttpServletResponse response )
	{
		final Categoria categoriaSalva = this.categoriaRepository.save( categoria );

		this.publisher.publishEvent( new RecursoCriadoEvent( this, response, categoriaSalva.getId() ) );

		return ResponseEntity.status( HttpStatus.CREATED ).body( categoriaSalva );
	}

	/**
	 *
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAnyAuthority('ROLE_PESQUISAR_CATEGORIA') and #oauth2.hasScope('read')")
	public ResponseEntity<Categoria> findCategoriaById( @PathVariable Long id )
	{
		final Categoria categoria = categoriaRepository.findOne( id );
		return categoria != null ? ResponseEntity.ok( categoria ) : ResponseEntity.notFound().build();
	}
}