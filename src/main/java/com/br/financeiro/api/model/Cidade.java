package com.br.financeiro.api.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "cidade")
public class Cidade
{
	@Id
	private Long id;

	private String nome;

	@ManyToOne
	@JoinColumn(name = "estado_id")
	private Estado estado;
}
