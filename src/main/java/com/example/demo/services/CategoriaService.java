package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.CategoriaRepository;
import com.example.demo.domain.Categoria;

import com.example.demo.dto.CategoriaDTO;
import com.example.demo.services.exception.DataIntegrityException;
import com.example.demo.services.exception.ObjectNotFoundException;

@Service
public class CategoriaService {

	@Autowired
	private CategoriaRepository repo;

	public Categoria find(Integer id) {
		Optional<Categoria> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objecto não encontrado! ID: " + id + ", Tipo" + Categoria.class.getName()));
	}

	public Categoria insert(Categoria obj) {
		obj.setId(null);
		return repo.save(obj);
	}

	public Categoria update(Categoria obj) {
		Categoria newObj= find(obj.getId());
		updateData(newObj, obj);
		return repo.save(obj);
	}

	public void delete(Integer id) {
		find(id);

		try {
			repo.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Nao é Possivel Excluir uma Categoria Que Possui Produtos");

		}

	}

	public List<Categoria> findAll() {

		return repo.findAll();
	}
	
	public Page<Categoria> findPage(Integer page,Integer linesPerPage,String orderBy,String direction){
		
		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
		
		return repo.findAll(pageRequest);
	}
	
	public Categoria fromDTO (CategoriaDTO objDto) {
		return new Categoria(objDto.getId(),objDto.getNome());
	}
	
private  void  updateData(Categoria newObj,Categoria obj) {
		
		newObj.setNome(obj.getNome());
		
	}

}
