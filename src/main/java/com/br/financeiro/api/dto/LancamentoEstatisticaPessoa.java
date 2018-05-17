package com.br.financeiro.api.dto;

import java.math.BigDecimal;

import com.br.financeiro.api.model.Pessoa;
import com.br.financeiro.api.model.TipoLancamento;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LancamentoEstatisticaPessoa
{
	private TipoLancamento tipo;
	private Pessoa pessoa;
	private BigDecimal total;

	public LancamentoEstatisticaPessoa( TipoLancamento tipo, Pessoa pessoa, BigDecimal total )
	{
		this.tipo = tipo;
		this.pessoa = pessoa;
		this.total = total;
	}
}
