package pe.edu.upeu.cinemax.service;

import pe.edu.upeu.cinemax.model.Cliente;

import java.util.List;
import java.util.Optional;

public interface IClienteService {

    List<Cliente> findAll();

    Optional<Cliente> findById(String dni);

    Cliente save(Cliente cliente);

    Cliente update(String dni, Cliente cliente);

    void delete(String dni);
}
