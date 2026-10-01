package pe.edu.upeu.cinemax.service;

import pe.edu.upeu.cinemax.model.Venta;

import java.util.ArrayList;
import java.util.List;

public class VentaService {
    private final List<Venta> ventas = new ArrayList<>();
    private int nextId = 1;
    private int nextBoleta = 1;

    public Venta registrar(Venta venta) {
        venta.setId(nextId++);
        venta.setNumeroBoleta(String.format("%08d", nextBoleta++));
        ventas.add(venta);
        return venta;
    }

    public List<Venta> listar() { return new ArrayList<>(ventas); }

    public void eliminar(int id) {
        ventas.removeIf(v -> v.getId() == id);
    }
}
