package pe.edu.upeu.cinemax.repository;

import pe.edu.upeu.cinemax.model.Venta;
import pe.edu.upeu.cinemax.model.VentaDetalle;
import pe.edu.upeu.cinemax.util.Formato;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VentaRepository extends AbstractJpaRepository<Venta, Long> {

    private static final String SELECT_BASE =
            "SELECT v.*, c.nombre AS nombre_cliente, u.nombre AS nombre_usuario "
                    + "FROM venta v "
                    + "JOIN cliente c ON c.dni = v.dni_cliente "
                    + "JOIN usuario u ON u.id_usuario = v.id_usuario ";

    @Override
    protected String getTableName() {
        return "venta";
    }

    @Override
    protected String getPkColumn() {
        return "id_venta";
    }

    @Override
    public List<Venta> findAll() {
        return executeQuery(SELECT_BASE + "ORDER BY v.fecha_venta DESC, v.id_venta DESC");
    }

    @Override
    public Optional<Venta> findById(Long id) {
        return executeQueryOne(SELECT_BASE + "WHERE v.id_venta = ?", id);
    }

    public boolean existsByCliente(String dni) {
        return executeExists("SELECT 1 FROM venta WHERE dni_cliente = ?", dni);
    }

    /** Registra la venta, sus detalles y descuenta el stock en una sola transacción. */
    public Venta save(Venta venta, List<VentaDetalle> detalles) {
        try (Connection conn = openConnection()) {
            conn.setAutoCommit(false);
            try {
                insert(conn, venta);

                for (VentaDetalle detalle : detalles) {
                    detalle.setIdVenta(venta.getIdVenta());
                    long idDetalle = executeInsertGetKey(conn,
                            "INSERT INTO venta_detalle (id_venta, id_producto, id_funcion, precio_unit, porce_util, "
                                    + "cantidad, neto_total, utilidad) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                            detalle.getIdVenta(), detalle.getIdProducto(), detalle.getIdFuncion(),
                            detalle.getPrecioUnit(), detalle.getPorceUtil(), detalle.getCantidad(),
                            detalle.getNetoTotal(), detalle.getUtilidad());
                    detalle.setIdVentaDetalle(idDetalle);

                    if (detalle.isControlaStock()) {
                        int filas = executeUpdate(conn,
                                "UPDATE producto SET stock = stock - ? WHERE id_producto = ? AND stock >= ?",
                                detalle.getCantidad(), detalle.getIdProducto(), detalle.getCantidad());
                        if (filas == 0) {
                            throw new IllegalStateException("Stock insuficiente para "
                                    + detalle.getNombreProducto() + ".");
                        }
                    }
                }

                conn.commit();
                return venta;
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof RuntimeException re) throw re;
                throw new RuntimeException("Error al registrar la venta: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al registrar la venta: " + e.getMessage(), e);
        }
    }

    /** Devuelve los detalles de una venta con el nombre del producto y la función. */
    public List<VentaDetalle> findDetalles(Long idVenta) {
        String sql = "SELECT d.*, p.nombre AS nombre_producto, p.controla_stock, "
                + "fu.fecha AS funcion_fecha, fu.hora AS funcion_hora, pe.titulo AS titulo_pelicula "
                + "FROM venta_detalle d "
                + "JOIN producto p ON p.id_producto = d.id_producto "
                + "LEFT JOIN funcion fu ON fu.id_funcion = d.id_funcion "
                + "LEFT JOIN pelicula pe ON pe.id_pelicula = fu.id_pelicula "
                + "WHERE d.id_venta = ? ORDER BY d.id_venta_detalle";

        try (Connection conn = openConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idVenta);
            try (ResultSet rs = ps.executeQuery()) {
                List<VentaDetalle> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapDetalle(rs));
                }
                return lista;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al consultar el detalle: " + e.getMessage(), e);
        }
    }

    /** Elimina la venta y devuelve el stock de los productos que lo controlan. */
    @Override
    public void deleteById(Long idVenta) {
        try (Connection conn = openConnection()) {
            conn.setAutoCommit(false);
            try {
                List<Object[]> stock = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement(
                        "SELECT d.id_producto, d.cantidad, p.controla_stock "
                                + "FROM venta_detalle d JOIN producto p ON p.id_producto = d.id_producto "
                                + "WHERE d.id_venta = ?")) {
                    ps.setLong(1, idVenta);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            if (rs.getBoolean("controla_stock")) {
                                stock.add(new Object[]{rs.getLong("id_producto"), rs.getInt("cantidad")});
                            }
                        }
                    }
                }

                for (Object[] fila : stock) {
                    executeUpdate(conn, "UPDATE producto SET stock = stock + ? WHERE id_producto = ?",
                            fila[1], fila[0]);
                }

                executeUpdate(conn, "DELETE FROM venta_detalle WHERE id_venta = ?", idVenta);
                executeUpdate(conn, "DELETE FROM venta WHERE id_venta = ?", idVenta);
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof RuntimeException re) throw re;
                throw new RuntimeException("Error al eliminar la venta: " + e.getMessage(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar la venta: " + e.getMessage(), e);
        }
    }

    @Override
    protected Venta insert(Connection conn, Venta e) throws SQLException {
        long id = executeInsertGetKey(conn,
                "INSERT INTO venta (dni_cliente, id_usuario, fecha_venta, neto_total, igv, precio_total) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                e.getDniCliente(), e.getIdUsuario(), java.sql.Date.valueOf(e.getFechaVenta()),
                e.getNetoTotal(), e.getIgv(), e.getPrecioTotal());
        e.setIdVenta(id);
        return e;
    }

    @Override
    protected Venta updateRow(Connection conn, Venta e) throws SQLException {
        executeUpdate(conn,
                "UPDATE venta SET dni_cliente = ?, id_usuario = ?, fecha_venta = ?, neto_total = ?, "
                        + "igv = ?, precio_total = ? WHERE id_venta = ?",
                e.getDniCliente(), e.getIdUsuario(), java.sql.Date.valueOf(e.getFechaVenta()),
                e.getNetoTotal(), e.getIgv(), e.getPrecioTotal(), e.getIdVenta());
        return e;
    }

    @Override
    protected Venta mapRow(ResultSet rs) throws SQLException {
        java.sql.Date fecha = rs.getDate("fecha_venta");
        return Venta.builder()
                .idVenta(rs.getLong("id_venta"))
                .dniCliente(rs.getString("dni_cliente"))
                .idUsuario(rs.getLong("id_usuario"))
                .fechaVenta(fecha == null ? null : fecha.toLocalDate())
                .netoTotal(rs.getBigDecimal("neto_total"))
                .igv(rs.getBigDecimal("igv"))
                .precioTotal(rs.getBigDecimal("precio_total"))
                .nombreCliente(rs.getString("nombre_cliente"))
                .nombreUsuario(rs.getString("nombre_usuario"))
                .build();
    }

    private VentaDetalle mapDetalle(ResultSet rs) throws SQLException {
        Long idFuncion = rs.getObject("id_funcion") == null ? null : rs.getLong("id_funcion");
        String descripcion = null;
        if (idFuncion != null) {
            java.sql.Date fecha = rs.getDate("funcion_fecha");
            java.sql.Time hora = rs.getTime("funcion_hora");
            descripcion = "F" + idFuncion + " · " + rs.getString("titulo_pelicula")
                    + " · " + (fecha == null ? "" : Formato.fecha(fecha.toLocalDate()))
                    + " " + (hora == null ? "" : Formato.hora(hora.toLocalTime()));
        }

        return VentaDetalle.builder()
                .idVentaDetalle(rs.getLong("id_venta_detalle"))
                .idVenta(rs.getLong("id_venta"))
                .idProducto(rs.getLong("id_producto"))
                .idFuncion(idFuncion)
                .precioUnit(rs.getBigDecimal("precio_unit"))
                .porceUtil(rs.getBigDecimal("porce_util"))
                .cantidad(rs.getInt("cantidad"))
                .netoTotal(rs.getBigDecimal("neto_total"))
                .utilidad(rs.getBigDecimal("utilidad"))
                .nombreProducto(rs.getString("nombre_producto"))
                .descripcionFuncion(descripcion)
                .controlaStock(rs.getBoolean("controla_stock"))
                .build();
    }
}
