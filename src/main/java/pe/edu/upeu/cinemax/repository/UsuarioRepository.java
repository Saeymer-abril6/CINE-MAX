package pe.edu.upeu.cinemax.repository;

import pe.edu.upeu.cinemax.model.Usuario;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UsuarioRepository extends AbstractJpaRepository<Usuario, Long> {

    @Override
    protected String getTableName() {
        return "usuario";
    }

    @Override
    protected String getPkColumn() {
        return "id_usuario";
    }

    /** Busca un usuario activo con sus credenciales. */
    public Optional<Usuario> login(String usuario, String clave) {
        return executeQueryOne(
                "SELECT u.*, p.nombre AS nombre_perfil "
                        + "FROM usuario u JOIN perfil p ON p.id_perfil = u.id_perfil "
                        + "WHERE u.usuario = ? AND u.clave = ? AND u.estado = 'Activo'",
                usuario, clave);
    }

    @Override
    protected Usuario insert(Connection conn, Usuario e) throws SQLException {
        long id = executeInsertGetKey(conn,
                "INSERT INTO usuario (usuario, clave, nombre, id_perfil, estado) VALUES (?, ?, ?, ?, ?)",
                e.getUsuario(), e.getClave(), e.getNombre(), e.getIdPerfil(), e.getEstado());
        e.setIdUsuario(id);
        return e;
    }

    @Override
    protected Usuario updateRow(Connection conn, Usuario e) throws SQLException {
        executeUpdate(conn,
                "UPDATE usuario SET usuario = ?, clave = ?, nombre = ?, id_perfil = ?, estado = ? WHERE id_usuario = ?",
                e.getUsuario(), e.getClave(), e.getNombre(), e.getIdPerfil(), e.getEstado(), e.getIdUsuario());
        return e;
    }

    @Override
    protected Usuario mapRow(ResultSet rs) throws SQLException {
        return Usuario.builder()
                .idUsuario(rs.getLong("id_usuario"))
                .usuario(rs.getString("usuario"))
                .clave(rs.getString("clave"))
                .nombre(rs.getString("nombre"))
                .idPerfil(rs.getLong("id_perfil"))
                .nombrePerfil(rs.getString("nombre_perfil"))
                .estado(rs.getString("estado"))
                .build();
    }
}
