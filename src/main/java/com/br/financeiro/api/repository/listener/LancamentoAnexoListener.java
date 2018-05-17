package com.br.financeiro.api.repository.listener;

import javax.persistence.PostLoad;

import org.springframework.util.StringUtils;

import com.br.financeiro.api.FinanceiroApplication;
import com.br.financeiro.api.model.Lancamento;
import com.br.financeiro.api.storage.S3;

public class LancamentoAnexoListener
{
	@PostLoad
	public void postLoad( Lancamento lancamento )
	{
		if ( StringUtils.hasText( lancamento.getAnexo() ) )
		{
			// Por não conseguirmos injetar a classe S3, precisamos pegar do Application que retorna a instância da classe que precisamos
			S3 s3 = FinanceiroApplication.getBean( S3.class );
			lancamento.setUrlAnexo( s3.configurarUrl( lancamento.getAnexo() ) );
		}
	}
}
