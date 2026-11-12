package pe.edu.upeu.cinemax.repository;

import pe.edu.upeu.cinemax.model.Sala;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class SalaRepository extends AbstractJpaRepository<Sala, Long> {

    @Override
    protected String getTableName() {
        return "sala";
    }

    @Override
    protected String getPkColumn() {
        return "id_sala";
    }

    @Override
    public List<Sala> findAll() {
        return executeQuery("SELECT * FROM sala ORDER BY nombre");
    }

    public java.util.Optional<Sala> findByNombre(String nombre) {
        return executeQueryOne("SELECT * FROM sala WHERE LOWER(nombre) = LOWER(?)", nombre);
    }

    @Override
    protected Sala insert(Connection conn, Sala e) throws SQLException {
        long id = executeInsertGetKey(conn,
                "INSERT INTO sala (nombre, tipo, capacidad) VALUES (?, ?, ?)",
                e.getNombre(), e.getTipo(), e.getCapacidad());
        e.setIdSala(id);
        return e;
    }

    @Override
    protected Sala updateRow(Connection conn, Sala e) throws SQLException {
        executeUpdate(conn, "UPDATE sala SET nombre = ?, tipo = ?, capacidad = ? WHERE id_sala = ?",
                e.getNombre(), e.getTipo(), e.getCapacidad(), e.getIdSala());
        return e;
    }

    @Override
    protected Sala mapRow(ResultSet rs) throws SQLException {
        return Sala.builder()
                .idSala(rs.getLong("id_sala"))
                .nombre(rs.getString("nombre"))
                .tipo(rs.getString("tipo"))
                .capacidad(rs.getInt("capacidad"))
                .build();
    }
}
