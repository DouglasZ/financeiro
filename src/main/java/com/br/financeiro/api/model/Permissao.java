package com.br.financeiro.api.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "permissao")
public class Permissao
{
	@Id
	private Long id;

	private String descricao;
}