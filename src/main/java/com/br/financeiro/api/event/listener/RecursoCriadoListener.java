package com.br.financeiro.api.event.listener;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import com.br.financeiro.api.event.RecursoCriadoEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Criamos o Listener para que ele possa ouvir o evento
 */
@Component
public class RecursoCriadoListener implements ApplicationListener<RecursoCriadoEvent>
{
	/**
	 *
	 */
	@Override
	public void onApplicationEvent( RecursoCriadoEvent recursoCriadoEvent )
	{
		final HttpServletResponse response = recursoCriadoEvent.getResponse();
		final Long id = recursoCriadoEvent.getId();

		this.adicionarHeaderLocation( response, id );
	}

	/**
	 * Adicionamos no Header a Location com a URI
	 */
	private void adicionarHeaderLocation( HttpServletResponse response, Long id )
	{
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri().path( "/{codigo}" )
				.buildAndExpand( id ).toUri();
		response.setHeader( "Location", uri.toASCIIString() );
	}
}
