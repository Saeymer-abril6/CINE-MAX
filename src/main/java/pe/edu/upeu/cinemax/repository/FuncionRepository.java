package pe.edu.upeu.cinemax.repository;

import pe.edu.upeu.cinemax.enums.EstadoFuncion;
import pe.edu.upeu.cinemax.model.Funcion;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class FuncionRepository extends AbstractJpaRepository<Funcion, Long> {

    private static final String SELECT_BASE =
            "SELECT f.*, p.titulo AS nombre_pelicula, s.nombre AS nombre_sala "
                    + "FROM funcion f "
                    + "JOIN pelicula p ON p.id_pelicula = f.id_pelicula "
                    + "JOIN sala s ON s.id_sala = f.id_sala ";

    @Override
    protected String getTableName() {
        return "funcion";
    }

    @Override
    protected String getPkColumn() {
        return "id_funcion";
    }

    @Override
    public List<Funcion> findAll() {
        return executeQuery(SELECT_BASE + "ORDER BY f.fecha, f.hora");
    }

    @Override
    public Optional<Funcion> findById(Long id) {
        return executeQueryOne(SELECT_BASE + "WHERE f.id_funcion = ?", id);
    }

    public Optional<Funcion> findBySalaFechaHora(Long idSala, LocalDate fecha, LocalTime hora) {
        return executeQueryOne(SELECT_BASE + "WHERE f.id_sala = ? AND f.fecha = ? AND f.hora = ?",
                idSala, java.sql.Date.valueOf(fecha), java.sql.Time.valueOf(hora));
    }

    public boolean existsByPelicula(Long idPelicula) {
        return executeExists("SELECT 1 FROM funcion WHERE id_pelicula = ?", idPelicula);
    }

    public boolean existsBySala(Long idSala) {
        return executeExists("SELECT 1 FROM funcion WHERE id_sala = ?", idSala);
    }

    public boolean existsEnVentaDetalle(Long idFuncion) {
        return executeExists("SELECT 1 FROM venta_detalle WHERE id_funcion = ?", idFuncion);
    }

    @Override
    protected Funcion insert(Connection conn, Funcion e) throws SQLException {
        long id = executeInsertGetKey(conn,
                "INSERT INTO funcion (id_pelicula, id_sala, fecha, hora, estado) VALUES (?, ?, ?, ?, ?)",
                e.getIdPelicula(), e.getIdSala(), java.sql.Date.valueOf(e.getFecha()),
                java.sql.Time.valueOf(e.getHora()), e.getEstado().getDescripcion());
        e.setIdFuncion(id);
        return e;
    }

    @Override
    protected Funcion updateRow(Connection conn, Funcion e) throws SQLException {
        executeUpdate(conn,
                "UPDATE funcion SET id_pelicula = ?, id_sala = ?, fecha = ?, hora = ?, estado = ? WHERE id_funcion = ?",
                e.getIdPelicula(), e.getIdSala(), java.sql.Date.valueOf(e.getFecha()),
                java.sql.Time.valueOf(e.getHora()), e.getEstado().getDescripcion(), e.getIdFuncion());
        return e;
    }

    @Override
    protected Funcion mapRow(ResultSet rs) throws SQLException {
        java.sql.Date fecha = rs.getDate("fecha");
        java.sql.Time hora = rs.getTime("hora");
        return Funcion.builder()
                .idFuncion(rs.getLong("id_funcion"))
                .idPelicula(rs.getLong("id_pelicula"))
                .idSala(rs.getLong("id_sala"))
                .fecha(fecha == null ? null : fecha.toLocalDate())
                .hora(hora == null ? null : hora.toLocalTime())
                .estado(EstadoFuncion.desde(rs.getString("estado")))
                .nombrePelicula(rs.getString("nombre_pelicula"))
                .nombreSala(rs.getString("nombre_sala"))
                .build();
    }
}
