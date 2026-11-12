package pe.edu.upeu.cinemax.repository;

import pe.edu.upeu.cinemax.model.UnidadMedida;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class UnidadMedidaRepository extends AbstractJpaRepository<UnidadMedida, Long> {

    @Override
    protected String getTableName() {
        return "unidad_medida";
    }

    @Override
    protected String getPkColumn() {
        return "id_unidad_medida";
    }

    @Override
    public List<UnidadMedida> findAll() {
        return executeQuery("SELECT * FROM unidad_medida ORDER BY nombre");
    }

    @Override
    protected UnidadMedida insert(Connection conn, UnidadMedida e) throws SQLException {
        long id = executeInsertGetKey(conn,
                "INSERT INTO unidad_medida (nombre, abreviatura) VALUES (?, ?)",
                e.getNombre(), e.getAbreviatura());
        e.setIdUnidadMedida(id);
        return e;
    }

    @Override
    protected UnidadMedida updateRow(Connection conn, UnidadMedida e) throws SQLException {
        executeUpdate(conn, "UPDATE unidad_medida SET nombre = ?, abreviatura = ? WHERE id_unidad_medida = ?",
                e.getNombre(), e.getAbreviatura(), e.getIdUnidadMedida());
        return e;
    }

    @Override
    protected UnidadMedida mapRow(ResultSet rs) throws SQLException {
        return UnidadMedida.builder()
                .idUnidadMedida(rs.getLong("id_unidad_medida"))
                .nombre(rs.getString("nombre"))
                .abreviatura(rs.getString("abreviatura"))
                .build();
    }
}
