package com.br.financeiro.api.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class Anexo
{
	private String nome;

	private  String url;

	public Anexo( String nome, String url )
	{
		this.nome = nome;
		this.url = url;
	}
}
