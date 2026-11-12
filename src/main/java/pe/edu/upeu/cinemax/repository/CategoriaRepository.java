package pe.edu.upeu.cinemax.repository;

import pe.edu.upeu.cinemax.model.Categoria;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CategoriaRepository extends AbstractJpaRepository<Categoria, Long> {

    @Override
    protected String getTableName() {
        return "categoria";
    }

    @Override
    protected String getPkColumn() {
        return "id_categoria";
    }

    @Override
    public List<Categoria> findAll() {
        return executeQuery("SELECT * FROM categoria ORDER BY nombre");
    }

    @Override
    protected Categoria insert(Connection conn, Categoria e) throws SQLException {
        long id = executeInsertGetKey(conn,
                "INSERT INTO categoria (nombre, descripcion) VALUES (?, ?)",
                e.getNombre(), e.getDescripcion());
        e.setIdCategoria(id);
        return e;
    }

    @Override
    protected Categoria updateRow(Connection conn, Categoria e) throws SQLException {
        executeUpdate(conn, "UPDATE categoria SET nombre = ?, descripcion = ? WHERE id_categoria = ?",
                e.getNombre(), e.getDescripcion(), e.getIdCategoria());
        return e;
    }

    @Override
    protected Categoria mapRow(ResultSet rs) throws SQLException {
        return Categoria.builder()
                .idCategoria(rs.getLong("id_categoria"))
                .nombre(rs.getString("nombre"))
                .descripcion(rs.getString("descripcion"))
                .build();
    }
}
