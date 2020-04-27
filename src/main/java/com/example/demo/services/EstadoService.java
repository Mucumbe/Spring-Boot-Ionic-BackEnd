package com.example.demo.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.EstadoRepository;
import com.example.demo.domain.Estado;

@Service
public class EstadoService {

    @Autowired
    private EstadoRepository repo;
    
    public List<Estado> findfAll(){
	
	return repo.findAllByOrderByNome();
    }
}
