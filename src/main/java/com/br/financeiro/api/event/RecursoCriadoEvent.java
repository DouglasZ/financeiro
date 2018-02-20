package com.br.financeiro.api.event;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;

public class RecursoCriadoEvent extends ApplicationEvent
{
	/*-------------------------------------------------------------------
 	 *				 		     ATTRIBUTES
 	 *-------------------------------------------------------------------*/
	/**
	 *
	 */
	private HttpServletResponse response;

	/**
	 *
	 */
	private Long id;

	/**
	 *
	 */
	//Estamos criando um evento
	public RecursoCriadoEvent( Object source, HttpServletResponse response, Long id )
	{
		super( source );
		this.response = response;
		this.id = id;
	}

	/**
	 *
	 */
	public HttpServletResponse getResponse()
	{
		return response;
	}

	/**
	 *
	 */
	public Long getId()
	{
		return id;
	}
}
