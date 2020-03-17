package com.example.demo.resources;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/categorias")
public class CategotiaResource {

	@RequestMapping(method = RequestMethod.GET)
	public String listar() {
		
		return "Reste esta a funcionar";
	}
}
