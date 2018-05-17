package com.br.financeiro.api.mail;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.br.financeiro.api.model.Lancamento;
import com.br.financeiro.api.model.Usuario;

@Component
public class Mailer
{
	private static final String REMETENTE = "teste@mail.com";

	private static final String TEMPLATE = "mail/aviso-lancamentos-vencidos.html";

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private TemplateEngine thymeleaf;

//	@Autowired
//	private LancamentoRepository repo;

//	/**
// 	 * APENAS PARA REALIZAR TESTE
//	 * Escuta um evento que o próprio Spring lança dizendo que a aplicação está pronta para ser utilizada/atender requisições
//	 */
//	@EventListener
//	private void test( ApplicationReadyEvent event )
//	{
//		this.enviarEmail( "testes.financeiro@gmail.com",
//				Arrays.asList("username@gmail.com"),
//				"Testando",
//				"Olá!<br/>Teste ok.");
//
//		System.out.println("Terminado o envio de email.");
//	}

//	/**
//	 *  APENAS PARA REALIZAR TESTE
//	 */
//	@EventListener
//	private void test( ApplicationReadyEvent event )
//	{
//		// A pasta "templates" não precisa por pois o Thymeleaf já entende que existe esta pasta
//		String template = "mail/aviso-lancamentos-vencidos.html";
//
//
//		List<Lancamento> lista = this.repo.findAll();
//
//		Map<String, Object> variaveis = new HashMap<>();
//		variaveis.put( "lancamentos", lista );
//
//		this.enviarEmail( "testes.financeiro@gmail.com",
//				Arrays.asList("username@gmail.com"),
//				"Testando",
//				template,
//				variaveis);
//
//		System.out.println("Terminado o envio de email.");
//	}

	/**
	 *
	 */
	public void avisarSobreLancamentosVencidos( List<Lancamento> lancamentosVencidos, List<Usuario> destinatarios )
	{
		Map<String, Object> variaveis = new HashMap<>();
		variaveis.put( "lancamentos", lancamentosVencidos );

		List<String> emails = destinatarios.stream()
				.map( usuario -> usuario.getEmail() )
				.collect( Collectors.toList() );

		this.enviarEmail( REMETENTE, emails, "Lançamentos vencidos", TEMPLATE, variaveis );
	}

	/**
	 *
	 */
	public void enviarEmail( String remetente, List<String> destinatarios, String assunto, String mensagem )
	{
		try
		{
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper( mimeMessage, "UTF-8" );
			helper.setFrom( remetente );
			helper.setTo( destinatarios.toArray( new String[destinatarios.size()] ) );
			helper.setSubject( assunto );
			helper.setText( mensagem, true );

			mailSender.send( mimeMessage );
		}
		catch ( MessagingException e )
		{
			throw new RuntimeException( "Problemas com o envio de email.", e );
		}
	}

	/**
	 *
	 */
	public void enviarEmail( String remetente, List<String> destinatarios, String assunto, String template, Map<String, Object> variaveis )
	{
		Context context = new Context( new Locale( "pt", "BR" ) );

		variaveis.entrySet().forEach( e -> context.setVariable( e.getKey(), e.getValue() ) );

		String mensagem = thymeleaf.process( template, context );

		this.enviarEmail( remetente, destinatarios, assunto, mensagem );
	}
}
