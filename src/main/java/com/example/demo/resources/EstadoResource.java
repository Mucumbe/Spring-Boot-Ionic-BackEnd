package com.example.demo.resources;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.Cidade;
import com.example.demo.domain.Estado;
import com.example.demo.dto.CidadeDTO;
import com.example.demo.dto.EstadoDTO;
import com.example.demo.services.CidadeService;
import com.example.demo.services.EstadoService;

@RestController
@RequestMapping(value = "/estados")
public class EstadoResource {

    @Autowired
    private EstadoService service;
    
    @Autowired
    private CidadeService cidadeservice;
    
    @RequestMapping(method =RequestMethod.GET)
    public ResponseEntity<List<EstadoDTO>> findAll(){
	
	List <Estado> list= service.findfAll();
	List<EstadoDTO> listDto=list.stream().map(obj-> new  EstadoDTO(obj)).collect(Collectors.toList());
	return ResponseEntity.ok().body(listDto);
    }
    
    @RequestMapping(value ="/{estadoId}/cidades", method =RequestMethod.GET   )
    public ResponseEntity<List<CidadeDTO>> findCidades(@PathVariable Integer estadoId){
	List<Cidade> list= cidadeservice.findByEstado(estadoId);
	List<CidadeDTO> listDto=list.stream().map(obj -> new CidadeDTO(obj)).collect(Collectors.toList());
	return ResponseEntity.ok().body(listDto);
	
    }
    
}
