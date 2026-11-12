package pe.edu.upeu.cinemax.service;

import pe.edu.upeu.cinemax.model.Venta;
import pe.edu.upeu.cinemax.model.VentaDetalle;

import java.util.List;

public interface IVentaService {

    List<Venta> findAll();

    List<VentaDetalle> findDetalles(Long idVenta);

    Venta registrar(Venta venta, List<VentaDetalle> detalles);

    void eliminar(Long idVenta);
}
