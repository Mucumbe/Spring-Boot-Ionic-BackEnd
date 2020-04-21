package com.example.demo.services;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Repositories.ItemPedidoRepository;
import com.example.demo.Repositories.PagamentoRepository;
import com.example.demo.Repositories.PedidoRepository;
import com.example.demo.domain.Cliente;
import com.example.demo.domain.ItemPedido;
import com.example.demo.domain.PagamentoComBoleto;
import com.example.demo.domain.Pedido;
import com.example.demo.domain.enums.EstadoPagamento;
import com.example.demo.security.UserSS;
import com.example.demo.services.exception.AuthorizationException;
import com.example.demo.services.exception.ObjectNotFoundException;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repo;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private BoletoService boletoService;

    @Autowired
    private PagamentoRepository pagamentoRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EmailService emailService;

    public Pedido find(Integer id) {
	Optional<Pedido> obj = repo.findById(id);
	return obj.orElseThrow(() -> new ObjectNotFoundException(
		"Objecto não encontrado! ID: " + id + ", Tipo" + Pedido.class.getName()));
    }

    @Transactional
    public Pedido insert(Pedido obj) {
	obj.setId(null);
	obj.setInstante(new Date());
	obj.setCliente(clienteService.find(obj.getCliente().getId()));
	obj.getPagamento().setEstado(EstadoPagamento.PENDENTE);
	obj.getPagamento().setPedido(obj);

	if (obj.getPagamento() instanceof PagamentoComBoleto) {
	    PagamentoComBoleto pagto = (PagamentoComBoleto) obj.getPagamento();
	    boletoService.preencherPagamentoComBoleto(pagto, obj.getInstante());
	}

	obj = repo.save(obj);
	pagamentoRepository.save(obj.getPagamento());

	for (ItemPedido ip : obj.getItens()) {
	    ip.setDesconto(0.0);
	    ip.setProduto(produtoService.find(ip.getProduto().getId()));
	    ip.setPreco(produtoService.find(ip.getProduto().getId()).getPreco());
	    ip.setPedido(obj);
	}
	itemPedidoRepository.saveAll(obj.getItens());

	emailService.sendOrderConfirmationHtmlEmail(obj);

	return obj;

    }

    public Page<Pedido> findPage(Integer page, Integer linesPerPage, String orderBy, String direction) {
	UserSS user = UserService.authenticated();
	if (user == null)
	    throw new AuthorizationException("Acesso negado");

	PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
	Cliente cliente =clienteService.find(user.getId());
	return repo.findByCliente(cliente, pageRequest); 
	
    }
}
