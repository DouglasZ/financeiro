package com.br.financeiro.api.repository;

import com.br.financeiro.api.model.Pessoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Long>
{

	Page<Pessoa> findByNomeContaining( String nome, Pageable pageable );
}