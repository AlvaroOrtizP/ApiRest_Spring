package com.pm.springboot.web.app.models.dao;

import org.springframework.data.repository.CrudRepository;

import com.pm.springboot.web.app.models.entity.Cliente;

public interface IClienteDao extends CrudRepository<Cliente, Long> {
	//CrudRepository pide dos parametros
	// 1 - El objeto entidad
	// 2 - El tipo de dato que tiene su Primary Key
}
