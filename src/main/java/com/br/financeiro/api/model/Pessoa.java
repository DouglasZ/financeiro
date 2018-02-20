package com.br.financeiro.api.model;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "pessoa")
public class Pessoa
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String nome;

	@Embedded
	private Endereco endereco;

	@NotNull
	private Boolean ativo;

	// Ignoramos pois o Jackson e o Hibernate vai achar que é uma propriedade e vaitentar serelizar ou tentar buscar
	@JsonIgnore
	@Transient
	public boolean isInativo()
	{
		return !this.ativo;
	}
}
