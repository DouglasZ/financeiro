package com.br.financeiro.api.exceptionhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Captura exceções de respostas de entidades
 */
@ControllerAdvice //Observa toda a aplicação
public class FinanceiroExceptionHandler extends ResponseEntityExceptionHandler
{
	/*-------------------------------------------------------------------
 	 *				 		     ATTRIBUTES
  	 *-------------------------------------------------------------------*/
	@Autowired
	private MessageSource messageSource;

	/*-------------------------------------------------------------------
	 *							BEHAVIORS
	 *-------------------------------------------------------------------*/

	/**
	 * Capituramos mensagens que é ilegíveis
	 */
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable( HttpMessageNotReadableException ex,
																   HttpHeaders headers, HttpStatus status, WebRequest request )
	{
		final String mensagemUsuario = this.messageSource.getMessage( "mensagem.invalida", null, LocaleContextHolder.getLocale() );
		final String mensagemDesenvolvedor = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
		final List<Erro> erros = Arrays.asList( new Erro( mensagemUsuario, mensagemDesenvolvedor ) );
		return handleExceptionInternal( ex, erros, headers, HttpStatus.BAD_REQUEST, request );
	}

	/**
	 * Capituramos argumentos de métodos que não são válidos
	 */
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid( MethodArgumentNotValidException ex,
																   HttpHeaders headers, HttpStatus status, WebRequest request )
	{
		final List<Erro> erros = this.criarListaDeErros( ex.getBindingResult() );
		return handleExceptionInternal( ex, erros, headers, HttpStatus.BAD_REQUEST, request );
	}

	/**
	 * Para quando tentar acessar um recurso que não existe
	 */
	@ExceptionHandler({EmptyResultDataAccessException.class})
	public ResponseEntity<Object> handleEmptyResultDataAccessException( EmptyResultDataAccessException ex, WebRequest request )
	{
		final String mensagemUsuario = this.messageSource.getMessage( "recurso.nao-encontrado", null, LocaleContextHolder.getLocale() );
		// Não foi preciso usar o getCause(), pois neste caso é exceção direto
		final String mensagemDesenvolvedor = ex.toString();
		final List<Erro> erros = Arrays.asList( new Erro( mensagemUsuario, mensagemDesenvolvedor ) );

		// O HttpHeaders não pode vim por paramentro neste caso, pois ocorre um erro
		return handleExceptionInternal( ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request );
	}

	/**
	 * Para quando tentar adicionar um identificador de referência que não existe no banco. Ex: Lançamento tem Pessoa e o Id de pessoa não existe no banco
	 */
	@ExceptionHandler({DataIntegrityViolationException.class})
	public ResponseEntity<Object> handleDataIntegrityViolationException( DataIntegrityViolationException ex, WebRequest request )
	{
		final String mensagemUsuario = this.messageSource.getMessage( "recurso.operacao-nao-permitida", null, LocaleContextHolder.getLocale() );
		// ExceptionUtils aprenseta uma mensagem da causa raiz da exceção
		final String mensagemDesenvolvedor = ExceptionUtils.getRootCauseMessage( ex );
		final List<Erro> erros = Arrays.asList( new Erro( mensagemUsuario, mensagemDesenvolvedor ) );

		// O HttpHeaders não pode vim por paramentro neste caso, pois ocorre um erro
		return handleExceptionInternal( ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request );
	}

	/**
	 * Criamos uma lista de erros
	 */
	private List<Erro> criarListaDeErros( BindingResult bindingResult )
	{
		final List<Erro> erros = new ArrayList<>();

		for ( FieldError fieldError : bindingResult.getFieldErrors() )
		{
			final String mensagemUsuario = this.messageSource.getMessage( fieldError, LocaleContextHolder.getLocale() );
			final String mensagemDesenvolvedor = fieldError.toString();
			erros.add( new Erro( mensagemUsuario, mensagemDesenvolvedor ) );
		}

		return erros;
	}

	/**
	 *
	 */
	public static class Erro
	{
		private String mensagemUsuario;
		private String mensagemDesenvolvedor;

		public Erro( String mensagemUsuario, String mensagemDesenvolvedor )
		{
			this.mensagemUsuario = mensagemUsuario;
			this.mensagemDesenvolvedor = mensagemDesenvolvedor;
		}

		public String getMensagemUsuario()
		{
			return mensagemUsuario;
		}

		public String getMensagemDesenvolvedor()
		{
			return mensagemDesenvolvedor;
		}
	}
}