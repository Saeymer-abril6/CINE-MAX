package pe.edu.upeu.cinemax.repository;

import pe.edu.upeu.cinemax.model.Cliente;

import java.util.ArrayList;
import java.util.List;

public class ClienteRepository {
    private final List<Cliente> clientes = new ArrayList<>();
    private int nextId = 1;

    public ClienteRepository() {
        guardar(new Cliente("00000000", "Cliente General", "999999999"));
    }

    public List<Cliente> listar() { return new ArrayList<>(clientes); }

    public Cliente guardar(Cliente cliente) {
        cliente.setId(nextId++);
        clientes.add(cliente);
        return cliente;
    }

    public void actualizar(Cliente cliente) {
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getId() == cliente.getId()) {
                clientes.set(i, cliente);
                return;
            }
        }
    }

    public void eliminar(int id) {
        if (id != 1) clientes.removeIf(c -> c.getId() == id);
    }
}
