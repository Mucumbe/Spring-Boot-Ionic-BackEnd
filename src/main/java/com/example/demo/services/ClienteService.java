package com.example.demo.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Repositories.ClienteRepository;
import com.example.demo.Repositories.EnderecoRepository;
import com.example.demo.domain.Cidade;
import com.example.demo.domain.Cliente;
import com.example.demo.domain.Endereco;
import com.example.demo.domain.enums.Perfil;
import com.example.demo.domain.enums.TipoCliente;
import com.example.demo.dto.ClienteDTO;
import com.example.demo.dto.ClienteNewDTO;
import com.example.demo.security.UserSS;
import com.example.demo.services.exception.AuthorizationException;
import com.example.demo.services.exception.DataIntegrityException;
import com.example.demo.services.exception.ObjectNotFoundException;

@Service
public class ClienteService {
    @Autowired
    private BCryptPasswordEncoder pe;

	@Autowired
	private ClienteRepository repo;
	
	@Autowired
	private EnderecoRepository enderecoRepository;

	

	public Cliente find(Integer id) {
	    
	    UserSS user= UserService.authenticated();
	    if(user==null || !user.hasRole(Perfil.ADMIN) && !id.equals(user.getId()))
		throw new AuthorizationException("Acesso Negado");
	    
		Optional<Cliente> obj = repo.findById(id);
		return obj.orElseThrow(() -> new ObjectNotFoundException(
				"Objecto não encontrado! ID: " + id + ", Tipo" + Cliente.class.getName()));
	}

	@Transactional
	public Cliente insert(Cliente obj) {
		obj.setId(null);
		obj=repo.save(obj);
		enderecoRepository.saveAll(obj.getEnderecos());
		return obj;
		
	}

	public Cliente update(Cliente obj) {
		Cliente newObj = find(obj.getId());
		updateData(newObj, obj);
		return repo.save(obj);
	}

	public void delete(Integer id) {
		find(id);

		try {
			repo.deleteById(id);
		} catch (DataIntegrityViolationException e) {
			throw new DataIntegrityException("Nao é Possivel Excluir porque ha Pedidos Relacionados relacionadas");

		}

	}

	public List<Cliente> findAll() {

		return repo.findAll();
	}

	public Page<Cliente> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {

		PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);

		return repo.findAll(pageRequest);
	}

	public Cliente fromDTO(ClienteDTO objDto) {

		return new Cliente(objDto.getId(), objDto.getNome(), objDto.getEmail(), null, null,null);
	}

	public Cliente fromDTO(ClienteNewDTO objDto) {
		Cliente cli = new Cliente(null, objDto.getNome(), objDto.getEmail(), objDto.getCpfoucnpj(),
				TipoCliente.toEnum(objDto.getTipo()),pe.encode(objDto.getSenha()));

		Cidade cid = new Cidade(objDto.getCidadeId(), null, null);

		Endereco end = new Endereco(null, objDto.getLogradouro(), objDto.getNumero(), objDto.getComplemento(),
				objDto.getBairro(), objDto.getCep(), cli, cid);

		cli.getEnderecos().add(end);
		cli.getTelefones().add(objDto.getTelefone1());

		if (objDto.getTelefone2() != null)
			cli.getTelefones().add(objDto.getTelefone2());
		
		if (objDto.getTelefone3() != null)
			cli.getTelefones().add(objDto.getTelefone3());
		
		return cli;
		

	}

	private void updateData(Cliente newObj, Cliente obj) {

		newObj.setNome(obj.getNome());
		newObj.setEmail(obj.getEmail());
	}
}
