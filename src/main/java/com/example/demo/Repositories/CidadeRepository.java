package com.example.demo.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.domain.Cidade;

public interface CidadeRepository  extends JpaRepository<Cidade, Integer>{

}
