package com.pm.springboot.web.app.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pm.springboot.web.app.models.entity.Cliente;
import com.pm.springboot.web.app.models.service.ClienteService;

import jakarta.validation.Valid;

//Para recibir llamadas de angular
@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping("/api")
public class ClienteRestController {

	@Autowired
	ClienteService clienteService;

	// http://localhost:8080/api/clientes
	@GetMapping("/clientes")
	public List<Cliente> index() {
		return clienteService.findAll();
	}

	// Por defecto si va bien da un 200
	// http://localhost:8080/api/clientes/1
	@GetMapping("/clientes/{id}")
	public ResponseEntity<?> show(@PathVariable Long id) {
		// Caso de error
		Map<String, Object> response = new HashMap<>();
		Cliente cliente = null;
		try {
			cliente = clienteService.findById(id);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al hacer la consulta ");
			response.put("error", e.getMostSpecificCause());

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (cliente == null) {
			response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);

		}
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
	}

	// Al venir el objeto en el cuerpo de un json se usa RequestBody
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {
		cliente.setId(null);
		cliente.setCreateAt(new Date());
		Map<String, Object> response = new HashMap<>();
		if (cliente.getNombre() == null || cliente.getNombre().equals("")) {
			cliente.setNombre(null);
		}

		if (result.hasErrors()) {
			//List<String> errors = new ArrayList<>();

			//for (FieldError err : result.getFieldErrors()) {
			//	errors.add("El campo " + err.getField() + " -" + err.getDefaultMessage());
			//}
			//
			//stream combierte a stream
			//map por cada registro retorna una entrada 
			//Despues lo trasformamos a una lista con collect(Collectors.toList()
			//err es de tipo FieldErrors
			List<String> errors  = result.getFieldErrors()
					.stream()
					.map(err -> {
					return "El campo " + err.getField() + " -" + err.getDefaultMessage();
					})
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			cliente = clienteService.save(cliente);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al hacer la consulta ");
			response.put("error", e.getMessage());

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "El cliente fue creado correctamente");
		response.put("cliente", cliente);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	
	//Importante que el BindingResult result estga antes del PathVariable
	@PutMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();
		Cliente clienteActual = clienteService.findById(id);

		if (clienteActual == null) {
			response.put("mensaje", "El cliente ID: ".concat(id.toString().concat(" no existe")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);

		}
		if (result.hasErrors()) {
			List<String> errors  = result.getFieldErrors()
					.stream()
					.map(err -> {
					return "El campo " + err.getField() + " -" + err.getDefaultMessage();
					})
					.collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		try {

			clienteActual.setApellido(cliente.getApellido());
			clienteActual.setEmail(cliente.getEmail());
			clienteActual.setNombre(cliente.getNombre());

			clienteActual = clienteService.save(clienteActual);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al ATUALIZAR");
			response.put("error", e.getMessage());

			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		response.put("mensaje", "El cliente fue creado correctamente");
		response.put("cliente", cliente);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);

	}

	@DeleteMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		clienteService.delete(id);
	}
}
