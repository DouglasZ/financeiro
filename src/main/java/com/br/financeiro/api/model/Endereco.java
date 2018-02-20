package com.br.financeiro.api.model;

import javax.persistence.Embeddable;

import lombok.Data;

@Data
@Embeddable
public class Endereco
{
	private String logradouro;
	private String numero;
	private String complemento;
	private String bairro;
	private String cep;
	private String cidade;
	private String estado;
}
