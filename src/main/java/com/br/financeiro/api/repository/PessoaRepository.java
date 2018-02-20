package com.br.financeiro.api.repository;

import com.br.financeiro.api.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Long>
{

}