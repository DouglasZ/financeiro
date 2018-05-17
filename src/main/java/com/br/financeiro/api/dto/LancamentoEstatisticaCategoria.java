package com.br.financeiro.api.dto;

import java.math.BigDecimal;

import com.br.financeiro.api.model.Categoria;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class LancamentoEstatisticaCategoria
{
	private Categoria categoria;

	private BigDecimal total;

	public LancamentoEstatisticaCategoria( Categoria categoria, BigDecimal total )
	{
		this.categoria = categoria;
		this.total = total;
	}
}
