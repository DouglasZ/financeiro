package com.br.financeiro.api.resource;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.br.financeiro.api.dto.Anexo;
import com.br.financeiro.api.dto.LancamentoEstatisticaCategoria;
import com.br.financeiro.api.dto.LancamentoEstatisticaDia;
import com.br.financeiro.api.event.RecursoCriadoEvent;
import com.br.financeiro.api.exceptionhandler.FinanceiroExceptionHandler.Erro;
import com.br.financeiro.api.model.Lancamento;
import com.br.financeiro.api.repository.LancamentoRepository;
import com.br.financeiro.api.repository.filter.LancamentoFilter;
import com.br.financeiro.api.repository.projection.ResumoLancamento;
import com.br.financeiro.api.service.LancamentoService;
import com.br.financeiro.api.service.exception.PessoaInexistenteOuInativaException;
import com.br.financeiro.api.storage.S3;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResource
{
	/*-------------------------------------------------------------------
	 *				 		     ATTRIBUTES
	 *-------------------------------------------------------------------*/
	/**
	 *
	 */
	@Autowired
	private LancamentoRepository lancamentoRepository;

	/**
	 *
	 */
	@Autowired
	private LancamentoService lancamentoService;

	/**
	 * Publicador de eventos de aplicação
	 */
	@Autowired
	private ApplicationEventPublisher publisher;

	/**
	 *
	 */
	@Autowired
	private MessageSource messageSource;

	/**
	 *
	 */
	@Autowired
	private S3 s3;

	/*-------------------------------------------------------------------
	 *								BEHAVIORS
	 *-------------------------------------------------------------------*/

	/**
	 *
	 */
	@GetMapping
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public Page<Lancamento> pesquisar( LancamentoFilter filter, Pageable pageable )
	{
		//Apenas para exmeplo, que podemos também usar o Criteria.
		//return this.lancamentoRepository.filtrar( filter, pageable );
		return this.lancamentoRepository.listByFilters( filter.getDescricao(), filter.getDataInicial(), filter.getDataFinal(), pageable );
	}

	/**
	 * Apenas para exemplo para fazer um consulta resumida em Criteria
	 */
	@GetMapping(params = "resumo")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public Page<ResumoLancamento> resumir( LancamentoFilter lancamentoFilter, Pageable pageable )
	{
		return this.lancamentoRepository.resumir( lancamentoFilter, pageable );
	}

	/**
	 *
	 */
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
	public ResponseEntity<Lancamento> criar( @Valid @RequestBody Lancamento lancamento, HttpServletResponse response )
	{
		final Lancamento lancamentoSalvo = this.lancamentoService.salvar( lancamento );

		this.publisher.publishEvent( new RecursoCriadoEvent( this, response, lancamentoSalvo.getId() ) );

		return ResponseEntity.status( HttpStatus.CREATED ).body( lancamentoSalvo );
	}

	/**
	 *
	 */
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity<Lancamento> findLancamentoById( @PathVariable Long id )
	{
		final Optional <Lancamento> lancamento = this.lancamentoRepository.findById( id );
		return lancamento.isPresent() ? ResponseEntity.ok( lancamento.get() ) : ResponseEntity.notFound().build();
	}

	/**
	 *
	 */
	@DeleteMapping("/{id}")
	// Deu OK porem devemos dizer que não tem nenhum conteúdo para retornar
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO') and #oauth2.hasScope('write')")
	public void remover( @PathVariable Long id )
	{
		this.lancamentoRepository.deleteById( id );
	}

	/**
	 *
	 */
	@PutMapping("/{codigo}")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO')")
	public ResponseEntity<Lancamento> atualizar( @PathVariable Long codigo, @Valid @RequestBody Lancamento lancamento )
	{
		try
		{
			final Lancamento lancamentoSalvo = this.lancamentoService.atualizar( codigo, lancamento );
			return ResponseEntity.ok( lancamentoSalvo );
		}
		catch ( IllegalArgumentException e )
		{
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 *
	 */
	// Não foi adicionando no ExceptionHandler pois essa mensagem é mais específica
	@ExceptionHandler({PessoaInexistenteOuInativaException.class})
	public ResponseEntity<Object> handlePessoaInexistenteOuInativaException( PessoaInexistenteOuInativaException ex )
	{
		final String mensagemUsuario = this.messageSource.getMessage( "pessoa.inexistente-ou-inativa", null, LocaleContextHolder.getLocale() );
		final String mensagemDesenvolvedor = ex.toString();
		List<Erro> erros = Arrays.asList( new Erro( mensagemUsuario, mensagemDesenvolvedor ) );
		return ResponseEntity.badRequest().body( erros );
	}

	// ------------ CONSULTAS PARA GRÁFICOS/RELATÓRIOS --------------------

	/**
	 *
	 */
	@GetMapping("/estatisticas/por-categoria")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public List<LancamentoEstatisticaCategoria> porCategoria()
	{
		return this.lancamentoRepository.porCategoria( LocalDate.now() );
	}

	/**
	 *
	 */
	@GetMapping("/estatisticas/por-dia")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public List<LancamentoEstatisticaDia> porDia()
	{
		return this.lancamentoRepository.porDia( LocalDate.now() );
	}

	/**
	 *
	 */
	@GetMapping("/relatorios/por-pessoa")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity<byte[]> relatorioPorPessoa(
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inicio,
			@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fim ) throws Exception
	{
		byte[] relatorio = this.lancamentoService.relatorioPorPessoa( inicio, fim );

		return ResponseEntity.ok().header( HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE ).body( relatorio );
	}

	// -------------------------------------------------------------------

	// ----------------------- UPLOAD DE ARQUIVOS ------------------------

	/**
	 *
	 */
	@PostMapping("/anexo")
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
	public Anexo uploadAnexo( @RequestParam MultipartFile anexo ) throws IOException
	{
		String nome = s3.salvarTemporariamente( anexo );

//		OutputStream out = new FileOutputStream( "D:/anexo-"+ anexo.getOriginalFilename() );
//		out.write( anexo.getBytes() );
//		out.close();
		return new Anexo( nome, s3.configurarUrl( nome ) );
	}

	// -------------------------------------------------------------------
}