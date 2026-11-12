package pe.edu.upeu.cinemax.service.impl;

import pe.edu.upeu.cinemax.enums.EstadoProducto;
import pe.edu.upeu.cinemax.exception.BusinessException;
import pe.edu.upeu.cinemax.model.Categoria;
import pe.edu.upeu.cinemax.model.Producto;
import pe.edu.upeu.cinemax.model.UnidadMedida;
import pe.edu.upeu.cinemax.repository.CategoriaRepository;
import pe.edu.upeu.cinemax.repository.ProductoRepository;
import pe.edu.upeu.cinemax.repository.UnidadMedidaRepository;
import pe.edu.upeu.cinemax.service.IProductoService;
import pe.edu.upeu.cinemax.util.Validador;

import java.util.List;

public class ProductoServiceImp implements IProductoService {

    private final ProductoRepository repository;
    private final CategoriaRepository categoriaRepository;
    private final UnidadMedidaRepository unidadRepository;

    public ProductoServiceImp(ProductoRepository repository,
                              CategoriaRepository categoriaRepository,
                              UnidadMedidaRepository unidadRepository) {
        this.repository = repository;
        this.categoriaRepository = categoriaRepository;
        this.unidadRepository = unidadRepository;
    }

    @Override
    public List<Producto> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Producto> findActivos() {
        return repository.findActivos();
    }

    @Override
    public List<Categoria> categorias() {
        return categoriaRepository.findAll();
    }

    @Override
    public List<UnidadMedida> unidades() {
        return unidadRepository.findAll();
    }

    @Override
    public Producto save(Producto producto) {
        validar(producto);
        repository.findByNombre(producto.getNombre()).ifPresent(p -> {
            throw new BusinessException("Ya existe un producto con el nombre " + producto.getNombre() + ".");
        });
        return repository.save(producto);
    }

    @Override
    public Producto update(Long id, Producto producto) {
        if (repository.findById(id).isEmpty()) {
            throw new BusinessException("El producto que intentas actualizar no existe.");
        }
        validar(producto);
        repository.findByNombre(producto.getNombre()).ifPresent(p -> {
            if (!p.getIdProducto().equals(id)) {
                throw new BusinessException("Ya existe otro producto con ese nombre.");
            }
        });
        producto.setIdProducto(id);
        return repository.update(producto);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new BusinessException("El producto que intentas eliminar no existe.");
        }
        if (repository.existsEnVentaDetalle(id)) {
            throw new BusinessException("No se puede eliminar el producto porque aparece en ventas registradas.");
        }
        repository.deleteById(id);
    }

    private void validar(Producto producto) {
        Validador.validar(producto);
        if (categoriaRepository.findById(producto.getIdCategoria()).isEmpty()) {
            throw new BusinessException("La categoría seleccionada no existe.");
        }
        if (unidadRepository.findById(producto.getIdUnidadMedida()).isEmpty()) {
            throw new BusinessException("La unidad de medida seleccionada no existe.");
        }
        if (!producto.isControlaStock()) {
            producto.setStock(0);
        }
        if (producto.getEstado() == null) {
            producto.setEstado(EstadoProducto.ACTIVO);
        }
    }
}
