package pe.edu.upeu.cinemax.service.impl;

import pe.edu.upeu.cinemax.exception.BusinessException;
import pe.edu.upeu.cinemax.model.Cliente;
import pe.edu.upeu.cinemax.repository.ClienteRepository;
import pe.edu.upeu.cinemax.repository.VentaRepository;
import pe.edu.upeu.cinemax.service.IClienteService;
import pe.edu.upeu.cinemax.util.Validador;

import java.util.List;
import java.util.Optional;

public class ClienteServiceImp implements IClienteService {

    private final ClienteRepository repository;
    private final VentaRepository ventaRepository;

    public ClienteServiceImp(ClienteRepository repository, VentaRepository ventaRepository) {
        this.repository = repository;
        this.ventaRepository = ventaRepository;
    }

    @Override
    public List<Cliente> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Cliente> findById(String dni) {
        return repository.findById(dni);
    }

    @Override
    public Cliente save(Cliente cliente) {
        Validador.validar(cliente);
        if (repository.existsById(cliente.getDni())) {
            throw new BusinessException("Ya existe un cliente con el DNI " + cliente.getDni() + ".");
        }
        return repository.save(cliente);
    }

    @Override
    public Cliente update(String dni, Cliente cliente) {
        if (!repository.existsById(dni)) {
            throw new BusinessException("El cliente que intentas actualizar no existe.");
        }
        cliente.setDni(dni);
        Validador.validar(cliente);
        return repository.update(cliente);
    }

    @Override
    public void delete(String dni) {
        if (!repository.existsById(dni)) {
            throw new BusinessException("El cliente que intentas eliminar no existe.");
        }
        if (ventaRepository.existsByCliente(dni)) {
            throw new BusinessException("No se puede eliminar el cliente porque tiene ventas registradas.");
        }
        repository.deleteById(dni);
    }
}
