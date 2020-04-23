package com.example.demo.services;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Repositories.ClienteRepository;
import com.example.demo.domain.Cliente;
import com.example.demo.services.exception.ObjectNotFoundException;

@Service
public class AuthService {

    @Autowired
    private ClienteRepository clienteRepository;

    private Random rand = new Random();

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Autowired
    private EmailService emailService;

    public void sendNewPassword(String email) {
	Cliente cliente = clienteRepository.findByEmail(email);
	if (cliente == null)
	    throw new ObjectNotFoundException("Email Nao encontrado");

	String newPass = newPassword();
	cliente.setSenha(bCryptPasswordEncoder.encode(newPass));
	clienteRepository.save(cliente);
	emailService.sendNewPasswordEmail(cliente, newPass);
    }

    private String newPassword() {
	char[] vet = new char[10];
	for (int i = 0; i < 10; i++)
	    vet[i] = randomChar();
	return new String(vet);
    }

    private char randomChar() {
	int opt = rand.nextInt(3);
	if (opt == 0) { //Gera numero aleatorio
	    return (char) (rand.nextInt(10) + 48);
	} else if (opt == 1) {  //gera letras mausculas
	    return (char) (rand.nextInt(26) + 65);
	} else {  //gera letras menusculas
	    return (char) (rand.nextInt(26) + 97);
	}
    }
}
