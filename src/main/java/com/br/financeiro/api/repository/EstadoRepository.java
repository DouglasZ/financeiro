package com.br.financeiro.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.br.financeiro.api.model.Estado;

public interface EstadoRepository extends JpaRepository<Estado, Long>
{

}