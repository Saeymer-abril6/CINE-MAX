package pe.edu.upeu.cinemax.service;

import pe.edu.upeu.cinemax.model.Categoria;
import pe.edu.upeu.cinemax.model.Producto;
import pe.edu.upeu.cinemax.model.UnidadMedida;

import java.util.List;

public interface IProductoService {

    List<Producto> findAll();

    List<Producto> findActivos();

    List<Categoria> categorias();

    List<UnidadMedida> unidades();

    Producto save(Producto producto);

    Producto update(Long id, Producto producto);

    void delete(Long id);
}
