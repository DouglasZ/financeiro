package com.br.financeiro.api.repository.filter;

import java.time.LocalDate;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class LancamentoFilter
{
	private String descricao;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dataInicial;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate dataFinal;
}
