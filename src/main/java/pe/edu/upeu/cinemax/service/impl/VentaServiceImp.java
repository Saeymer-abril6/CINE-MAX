package pe.edu.upeu.cinemax.service.impl;

import pe.edu.upeu.cinemax.enums.EstadoProducto;
import pe.edu.upeu.cinemax.exception.BusinessException;
import pe.edu.upeu.cinemax.model.Producto;
import pe.edu.upeu.cinemax.model.Venta;
import pe.edu.upeu.cinemax.model.VentaDetalle;
import pe.edu.upeu.cinemax.repository.ClienteRepository;
import pe.edu.upeu.cinemax.repository.FuncionRepository;
import pe.edu.upeu.cinemax.repository.ProductoRepository;
import pe.edu.upeu.cinemax.repository.VentaRepository;
import pe.edu.upeu.cinemax.service.IVentaService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VentaServiceImp implements IVentaService {

    private static final BigDecimal TASA_IGV = new BigDecimal("0.18");
    private static final BigDecimal UNO_MAS_IGV = BigDecimal.ONE.add(TASA_IGV);

    private final VentaRepository repository;
    private final ProductoRepository productoRepository;
    private final ClienteRepository clienteRepository;
    private final FuncionRepository funcionRepository;

    public VentaServiceImp(VentaRepository repository,
                           ProductoRepository productoRepository,
                           ClienteRepository clienteRepository,
                           FuncionRepository funcionRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
        this.clienteRepository = clienteRepository;
        this.funcionRepository = funcionRepository;
    }

    @Override
    public List<Venta> findAll() {
        return repository.findAll();
    }

    @Override
    public List<VentaDetalle> findDetalles(Long idVenta) {
        return repository.findDetalles(idVenta);
    }

    /**
     * Valida la venta, calcula precios, IGV, total y utilidad,
     * y guarda la cabecera con sus detalles.
     */
    @Override
    public Venta registrar(Venta venta, List<VentaDetalle> detalles) {
        if (venta == null) {
            throw new BusinessException("No hay datos de la venta.");
        }
        if (venta.getDniCliente() == null || venta.getDniCliente().isBlank()) {
            throw new BusinessException("Selecciona un cliente.");
        }
        if (!clienteRepository.existsById(venta.getDniCliente())) {
            throw new BusinessException("El cliente seleccionado no existe.");
        }
        if (venta.getFechaVenta() == null) {
            throw new BusinessException("La fecha de la venta es obligatoria.");
        }
        if (venta.getIdUsuario() == null) {
            throw new BusinessException("No hay un usuario en sesión.");
        }
        if (detalles == null || detalles.isEmpty()) {
            throw new BusinessException("Agrega al menos un producto o entrada a la venta.");
        }

        Map<Long, Integer> cantidadesPorProducto = new HashMap<>();
        BigDecimal total = BigDecimal.ZERO;

        for (VentaDetalle detalle : detalles) {
            if (detalle.getIdProducto() == null) {
                throw new BusinessException("Selecciona un producto en cada línea del detalle.");
            }
            if (detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new BusinessException("La cantidad debe ser mayor a 0.");
            }

            Producto producto = productoRepository.findById(detalle.getIdProducto())
                    .orElseThrow(() -> new BusinessException("El producto seleccionado no existe."));

            if (producto.getEstado() != EstadoProducto.ACTIVO) {
                throw new BusinessException("El producto " + producto.getNombre() + " está inactivo.");
            }

            boolean esEntrada = "Entradas".equalsIgnoreCase(producto.getNombreCategoria());
            if (esEntrada) {
                if (detalle.getIdFuncion() == null) {
                    throw new BusinessException("Selecciona la función para la entrada " + producto.getNombre() + ".");
                }
                if (!funcionRepository.existsById(detalle.getIdFuncion())) {
                    throw new BusinessException("La función seleccionada no existe.");
                }
            } else {
                detalle.setIdFuncion(null);
            }

            int acumulado = cantidadesPorProducto.merge(producto.getIdProducto(), detalle.getCantidad(), Integer::sum);
            if (producto.isControlaStock() && acumulado > producto.getStock()) {
                throw new BusinessException("Stock insuficiente para " + producto.getNombre()
                        + ". Disponible: " + producto.getStock() + ".");
            }

            BigDecimal precio = producto.getPrecioVenta();
            BigDecimal neto = precio.multiply(BigDecimal.valueOf(detalle.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);
            BigDecimal utilidad = precio.subtract(producto.getCosto())
                    .multiply(BigDecimal.valueOf(detalle.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);

            detalle.setPrecioUnit(precio);
            detalle.setPorceUtil(producto.getPorceUtil());
            detalle.setNetoTotal(neto);
            detalle.setUtilidad(utilidad);
            detalle.setNombreProducto(producto.getNombre());
            detalle.setControlaStock(producto.isControlaStock());

            total = total.add(neto);
        }

        total = total.setScale(2, RoundingMode.HALF_UP);
        BigDecimal netoVenta = total.divide(UNO_MAS_IGV, 2, RoundingMode.HALF_UP);
        BigDecimal igv = total.subtract(netoVenta).setScale(2, RoundingMode.HALF_UP);

        venta.setPrecioTotal(total);
        venta.setNetoTotal(netoVenta);
        venta.setIgv(igv);

        return repository.save(venta, detalles);
    }

    @Override
    public void eliminar(Long idVenta) {
        if (!repository.existsById(idVenta)) {
            throw new BusinessException("La venta que intentas eliminar no existe.");
        }
        repository.deleteById(idVenta);
    }
}
