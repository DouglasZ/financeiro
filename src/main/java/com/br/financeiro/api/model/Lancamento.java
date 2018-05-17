package com.br.financeiro.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.br.financeiro.api.repository.listener.LancamentoAnexoListener;

import lombok.Data;
import lombok.EqualsAndHashCode;

// Toda vez que um lançamento for carregado do banco, o @EntityListeners será disparado e o método dentro dele será executado.
@EntityListeners(LancamentoAnexoListener.class)
@Data
@Entity
@EqualsAndHashCode
@Table(name = "lancamento")
public class Lancamento
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String descricao;

	@NotNull
	@Column(name = "data_vencimento")
	private LocalDate dataVencimento;

	@Column(name = "data_pagamento")
	private LocalDate dataPagamento;

	@NotNull
	private BigDecimal valor;

	private String observacao;

	@NotNull
	@Enumerated(EnumType.STRING)
	private TipoLancamento tipo;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "categoria_id")
	private Categoria categoria;

	@JsonIgnoreProperties("contatos")
	@NotNull
	@ManyToOne
	@JoinColumn(name = "pessoa_id")
	private Pessoa pessoa;

	private String anexo;

	@Transient
	private String urlAnexo;

	@JsonIgnore
	public boolean isReceita()
	{
		return TipoLancamento.RECEITA.equals( this.tipo );
	}
}