package com.br.financeiro.api.service;

import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.br.financeiro.api.dto.LancamentoEstatisticaPessoa;
import com.br.financeiro.api.mail.Mailer;
import com.br.financeiro.api.model.Lancamento;
import com.br.financeiro.api.model.Pessoa;
import com.br.financeiro.api.model.Usuario;
import com.br.financeiro.api.repository.LancamentoRepository;
import com.br.financeiro.api.repository.PessoaRepository;
import com.br.financeiro.api.repository.UsuarioRepository;
import com.br.financeiro.api.service.exception.PessoaInexistenteOuInativaException;
import com.br.financeiro.api.storage.S3;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class LancamentoService
{
	private static final String DESTINATARIOS = "ROLE_PESQUISAR_LANCAMENTO";

	private static final Logger logger = LoggerFactory.getLogger( LancamentoService.class );
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
	private UsuarioRepository usuarioRepository;

	/**
	 *
	 */
	@Autowired
	private MessageSource messageSource;

	/**
	 *
	 */
	@Autowired
	private Mailer mailer;

	/**
	 *
	 */
	@Autowired
	private S3 s3;

	/**
	 *
	 */
	public Lancamento salvar( Lancamento lancamento )
	{
		this.validarPessoa( lancamento );

		// Essa verificação é se caso for usar no Amazon S3 para poder salvar o arquivo permanente.
//		if( StringUtils.hasText( lancamento.getAnexo() ))
//		{
//			s3.salvar(lancamento.getAnexo());
//		}

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

		if ( StringUtils.isEmpty( lancamento.getAnexo() ) && StringUtils.hasText( lancamentoSalvo.getAnexo() ) )
		{
			s3.remover( lancamentoSalvo.getAnexo() );
		}
		else if ( StringUtils.hasText( lancamento.getAnexo() ) && !lancamento.getAnexo().equals( lancamentoSalvo.getAnexo() ) )
		{
			s3.substituir( lancamentoSalvo.getAnexo(), lancamento.getAnexo() );
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
			pessoa = pessoaRepository.getOne( lancamento.getPessoa().getId() );
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
		final Optional<Lancamento> lancamentoSalvo = this.lancamentoRepository.findById( id );

		Assert.isTrue( lancamentoSalvo.isPresent(),
				this.messageSource.getMessage( "notFoundById", new Object[]{id}, LocaleContextHolder.getLocale() ) );

//		if ( lancamentoSalvo == null )
//		{
//			throw new IllegalArgumentException();
//		}

		return lancamentoSalvo.get();
	}


	// ----- RELATÓRIO POR PESSOA

	/**
	 *
	 */
	public byte[] relatorioPorPessoa( LocalDate inicio, LocalDate fim ) throws Exception
	{
		List<LancamentoEstatisticaPessoa> dados = this.lancamentoRepository.porPessoa( inicio, fim );

		Map<String, Object> parametros = new HashMap<>();
		parametros.put( "DT_INICIO", Date.valueOf( inicio ) );
		parametros.put( "DT_FIM", Date.valueOf( fim ) );
		parametros.put( "REPORT_LOCALE", new Locale( "pt", "BR" ) );

		InputStream inputStream = this.getClass().getResourceAsStream( "/relatorios/lancamentos-por-pessoa.jasper" );

		JasperPrint jasperPrint = JasperFillManager.fillReport( inputStream, parametros, new JRBeanCollectionDataSource( dados ) );

		return JasperExportManager.exportReportToPdf( jasperPrint );
	}

	// ------- AGENDAMENTO DE SERVIÇO ----------------------------------------------------

	/**
	 * Criamos um agendamento para enviar um email com os lançamentos vencidos
	 */
	// > O @Scheduled(fixedDelay = 1000 * 2) não deixa encavaladar as execuções, ou seja, ele só inicia a próxima tarefa quando a chamada anterior terminar
	// > O cron é executado em um horário fixo
	@Scheduled(cron = "0 0 6 * * *")
	public void avisarSobreLancamentosVencidos()
	{
		if ( logger.isDebugEnabled() )
		{
			logger.debug( "Preparando envio de emails de aviso de lançamentos vencidos." );
		}

		List<Lancamento> lancamentosVencidos = this.lancamentoRepository
				.findByDataVencimentoLessThanEqualAndDataPagamentoIsNull( LocalDate.now() );

		if ( lancamentosVencidos.isEmpty() )
		{
			logger.info( "Sem lançamentos vencidos para aviso." );
			return;
		}

		logger.info( "Existem {} lançamentos vencidos.", lancamentosVencidos.size() );

		List<Usuario> destinatarios = this.usuarioRepository
				.findByPermissoesDescricao( DESTINATARIOS );

		if ( destinatarios.isEmpty() )
		{
			logger.warn( "Existem lançamentos vencidos, mas o sistema não encontrou destinatários." );
			return;
		}

		mailer.avisarSobreLancamentosVencidos( lancamentosVencidos, destinatarios );

		logger.info( "Envio de email de aviso concluído." );
	}

	// -----------------------------------------------------------------------------------
}