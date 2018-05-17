package com.br.financeiro.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.br.financeiro.api.model.TipoLancamento;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LancamentoEstatisticaDia
{
	private TipoLancamento tipo;

	private LocalDate dia;

	private BigDecimal total;

	public LancamentoEstatisticaDia( TipoLancamento tipo, LocalDate dia, BigDecimal total )
	{
		this.tipo = tipo;
		this.dia = dia;
		this.total = total;
	}
}
