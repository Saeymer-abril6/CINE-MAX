package pe.edu.upeu.cinemax.service;

import pe.edu.upeu.cinemax.model.Cliente;
import pe.edu.upeu.cinemax.repository.ClienteRepository;

import java.util.List;

public class ClienteService {
    private final ClienteRepository repo = new ClienteRepository();

    public List<Cliente> listar() { return repo.listar(); }
    public Cliente crear(String dni, String nombre, String telefono) {
        return repo.guardar(new Cliente(dni, nombre, telefono));
    }
    public void actualizar(Cliente cliente) { repo.actualizar(cliente); }
    public void eliminar(int id) { repo.eliminar(id); }
}
