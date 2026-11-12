package pe.edu.upeu.cinemax.repository;

import pe.edu.upeu.cinemax.enums.EstadoProducto;
import pe.edu.upeu.cinemax.model.Producto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ProductoRepository extends AbstractJpaRepository<Producto, Long> {

    private static final String SELECT_BASE =
            "SELECT p.*, c.nombre AS nombre_categoria, u.nombre AS nombre_unidad "
                    + "FROM producto p "
                    + "JOIN categoria c ON c.id_categoria = p.id_categoria "
                    + "JOIN unidad_medida u ON u.id_unidad_medida = p.id_unidad_medida ";

    @Override
    protected String getTableName() {
        return "producto";
    }

    @Override
    protected String getPkColumn() {
        return "id_producto";
    }

    @Override
    public List<Producto> findAll() {
        return executeQuery(SELECT_BASE + "ORDER BY c.nombre, p.nombre");
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return executeQueryOne(SELECT_BASE + "WHERE p.id_producto = ?", id);
    }

    public List<Producto> findActivos() {
        return executeQuery(SELECT_BASE + "WHERE p.estado = 'Activo' ORDER BY p.nombre");
    }

    public Optional<Producto> findByNombre(String nombre) {
        return executeQueryOne(SELECT_BASE + "WHERE LOWER(p.nombre) = LOWER(?)", nombre);
    }

    public boolean existsEnVentaDetalle(Long idProducto) {
        return executeExists("SELECT 1 FROM venta_detalle WHERE id_producto = ?", idProducto);
    }

    @Override
    protected Producto insert(Connection conn, Producto e) throws SQLException {
        long id = executeInsertGetKey(conn,
                "INSERT INTO producto (nombre, id_categoria, id_unidad_medida, costo, porce_util, stock, controla_stock, estado) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                e.getNombre(), e.getIdCategoria(), e.getIdUnidadMedida(), e.getCosto(),
                e.getPorceUtil(), e.getStock(), e.isControlaStock(), e.getEstado().getDescripcion());
        e.setIdProducto(id);
        return e;
    }

    @Override
    protected Producto updateRow(Connection conn, Producto e) throws SQLException {
        executeUpdate(conn,
                "UPDATE producto SET nombre = ?, id_categoria = ?, id_unidad_medida = ?, costo = ?, "
                        + "porce_util = ?, stock = ?, controla_stock = ?, estado = ? WHERE id_producto = ?",
                e.getNombre(), e.getIdCategoria(), e.getIdUnidadMedida(), e.getCosto(),
                e.getPorceUtil(), e.getStock(), e.isControlaStock(), e.getEstado().getDescripcion(),
                e.getIdProducto());
        return e;
    }

    @Override
    protected Producto mapRow(ResultSet rs) throws SQLException {
        return Producto.builder()
                .idProducto(rs.getLong("id_producto"))
                .nombre(rs.getString("nombre"))
                .idCategoria(rs.getLong("id_categoria"))
                .idUnidadMedida(rs.getLong("id_unidad_medida"))
                .costo(rs.getBigDecimal("costo"))
                .porceUtil(rs.getBigDecimal("porce_util"))
                .stock(rs.getInt("stock"))
                .controlaStock(rs.getBoolean("controla_stock"))
                .estado(EstadoProducto.desde(rs.getString("estado")))
                .nombreCategoria(rs.getString("nombre_categoria"))
                .nombreUnidad(rs.getString("nombre_unidad"))
                .build();
    }
}
