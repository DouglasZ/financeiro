package com.br.financeiro.api.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Entity
@EqualsAndHashCode
@Table(name = "contato")
@ToString(exclude = "pessoa")
public class Contato
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotEmpty
	private String nome;

	@Email
	@NotNull
	private String email;

	@NotEmpty
	private String telefone;

	@ManyToOne
	@JoinColumn(name = "pessoa_id")
	private Pessoa pessoa;
}
