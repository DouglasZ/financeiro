package com.br.financeiro.api.repository;

import com.br.financeiro.api.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>
{

}